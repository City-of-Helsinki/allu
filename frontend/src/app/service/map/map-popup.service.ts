import * as L from 'leaflet';
import {ALLU_PREFIX, isWinkkiId} from './map-layer-id';
import {findTranslation} from '@util/translations';
import {ApplicationRef, ComponentFactoryResolver, Inject, Injectable, Injector, LOCALE_ID} from '@angular/core';
import {MapPopupComponent, MapPopupContentRow} from '@feature/map/map-popup.component';
import {Feature, GeometryObject} from 'geojson';
import {ArrayUtil} from '@util/array-util';
import {recurringDateString} from '@feature/application/info/recurring/recurring-type';
import {formatDate} from '@angular/common';

@Injectable()
export class MapPopupService {

  constructor(private cfr: ComponentFactoryResolver,
              private injector: Injector,
              private appRef: ApplicationRef,
              @Inject(LOCALE_ID) private localeId: string) {
  }

  create(features: Feature<GeometryObject>[]): HTMLElement {
    if (features.length) {
      const entries = this.removeDuplicateEntries(features);
      const header = this.createHeader(entries);
      const content = this.createContent(entries);
      return this.createPopup(header, content);
    } else {
      throw Error('Popup requires at least one feature');
    }
  }

  private createHeader(features: Feature<GeometryObject>[]): string {
    if (features.length > 1) {
      return findTranslation('map.popup.titleMultipleFeatures', {count: String(features.length)});
    } else {
      return this.createSingleFeatureHeader(features[0]);
    }
  }

  private createSingleFeatureHeader(feature: Feature<GeometryObject>): string {
    const properties = feature.properties;
    const featureId = feature.id.toString();

    if (featureId.indexOf(ALLU_PREFIX) >= 0) {
      return properties.applicationId;
    } else if (isWinkkiId(featureId)) {
      return properties.licence_identifier;
    } else {
      throw new Error(`Unknown feature id ${featureId}`);
    }
  }

  private createContent(features: Feature<GeometryObject>[]): MapPopupContentRow[] {
    return features.length > 1
      ? this.createSimplifiedContent(features)
      : this.createDetailedContent(features[0]);
  }

  private createDetailedContent(feature: Feature<GeometryObject>): MapPopupContentRow[] {
    const properties = feature.properties;
    const featureId = feature.id.toString();
    let contentRows = [];

    if (featureId.indexOf(ALLU_PREFIX) >= 0) {
      contentRows = [
        this.createContentRowLink(properties.name, properties.id, 'content-row-bold'),
        this.createContentRow(properties.applicant),
        this.createDateContentRow(properties.startTime, properties.endTime, this.localeId, properties.recurringEndTime)
      ];

      if (properties.terminationTime) {
        contentRows.push(this.createTerminationContentRow(properties.terminationTime, this.localeId));
      }
    } else if (isWinkkiId(featureId)) {
      contentRows = [
        this.createContentRow(properties.event_description, 'content-row-bold'),
        this.createContentRow(`${properties.lic_startdate_txt} - ${properties.lic_enddate_txt}`)
      ];
    } else {
      throw new Error(`Unknown feature id ${feature.id}`);
    }

    return contentRows;
  }

  private createSimplifiedContent(features: Feature<GeometryObject>[]): MapPopupContentRow[] {
    const contentRows = [];
    features.forEach(f => {
      const properties = f.properties;
      const featureId = f.id.toString();
      if (featureId.indexOf(ALLU_PREFIX) >= 0) {
        contentRows.push(
          this.createContentRowLink(properties.applicationId, properties.id, 'content-row-bold'),
          this.createContentRow(properties.applicant),
          this.createDateContentRow(properties.startTime, properties.endTime, this.localeId, properties.recurringEndTime));

        if (properties.terminationTime) {
          contentRows.push(this.createTerminationContentRow(properties.terminationTime, this.localeId));
        }
      } else if (isWinkkiId(featureId)) {
        contentRows.push(
          this.createContentRow(properties.licence_identifier, 'content-row-bold'),
          this.createContentRow(`${properties.lic_startdate_txt} - ${properties.lic_enddate_txt}`));
      }
    });
    return contentRows;
  }

  private createContentRow(content: string, className?: string): MapPopupContentRow {
    return {
      content: content,
      class: className
    };
  }

  private createContentRowLink(content: string,  id: number, className?: string): MapPopupContentRow {
    return {
      content: content,
      link: `/applications/${id}/summary`,
      class: className,
      idForBasket: id
    };
  }

  private createDateContentRow(startTime: Date, endTime: Date, localeId: string, recurringEndTime?: Date): MapPopupContentRow {
    return this.createContentRow(
      recurringDateString(startTime, endTime, localeId, recurringEndTime),
      'single-line'
    );
  }

  private createTerminationContentRow(terminationTime: Date, localeId: string): MapPopupContentRow {
    const terminatedExpires = findTranslation('application.common.terminatedExpires');
    const formattedDate = formatDate(terminationTime, 'shortDate', localeId);
    return this.createContentRow(`${terminatedExpires} ${formattedDate}`);
  }

  private createPopup(header: string, contentRows: MapPopupContentRow[]): HTMLElement {
    const popup = L.DomUtil.create('div', 'popup-wrapper');
    const cmpFactory = this.cfr.resolveComponentFactory(MapPopupComponent);
    const componentRef = cmpFactory.create(this.injector);
    componentRef.instance.header = header;
    componentRef.instance.contentRows = contentRows;
    this.appRef.attachView(componentRef.hostView);
    popup.appendChild(componentRef.location.nativeElement);
    return popup;
  }

  private removeDuplicateEntries(features: Feature<GeometryObject>[]): Feature<GeometryObject>[] {
    return features.filter(ArrayUtil.uniqueItem((item: Feature<GeometryObject>) => item.id));
  }
}
