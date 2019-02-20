import * as L from 'leaflet';
import {ALLU_PREFIX, isWinkkiId} from './map-layer-id';
import {findTranslation} from '../../util/translations';
import {ApplicationRef, ComponentFactoryResolver, Injectable, Injector} from '@angular/core';
import {MapPopupComponent, MapPopupContentRow} from '../../feature/map/map-popup.component';

@Injectable()
export class MapPopupService {

  constructor(private cfr: ComponentFactoryResolver,
              private injector: Injector,
              private appRef: ApplicationRef) {
  }

  create(features: any[]): HTMLElement {
    if (features.length) {
      const header = this.createHeader(features);
      const content = this.createContent(features);
      return this.createPopup(header, content);
    } else  {
      throw Error('Popup requires at least one feature');
    }
  }

  private createHeader(features: any[]): string {
    if (features.length > 1) {
      return findTranslation('map.popup.titleMultipleFeatures', {count: String(features.length)});
    } else {
      return this.createSingeFeatureHeader(features[0]);
    }
  }

  private createSingeFeatureHeader(feature: any): string {
    const properties = feature.properties;

    if (feature.id.indexOf(ALLU_PREFIX) >= 0) {
      return properties.applicationId;
    } else if (isWinkkiId(feature.id)) {
      return properties.licence_identifier;
    } else {
      throw new Error(`Unknown feature id ${feature.id}`);
    }
  }

  private createContent(features: any[]): MapPopupContentRow[] {
    return features.length > 1
      ? this.createSimplifiedContent(features)
      : this.createDetailedContent(features[0]);
  }

  private createDetailedContent(feature: any): MapPopupContentRow[] {
    const properties = feature.properties;
    let contentRows = [];

    if (feature.id.indexOf(ALLU_PREFIX) >= 0) {
      contentRows = [
        this.createContentRowLink(properties.name, properties.id, 'content-row-bold'),
        this.createContentRow(properties.applicant),
        this.createContentRow(`${properties.startTime} - ${properties.endTime}`)
      ];
    } else if (isWinkkiId(feature.id)) {
      contentRows = [
        this.createContentRow(properties.event_description, 'content-row-bold'),
        this.createContentRow(`${properties.lic_startdate_txt} - ${properties.lic_enddate_txt}`)
      ];
    } else {
      throw new Error(`Unknown feature id ${feature.id}`);
    }

    return contentRows;
  }

  private createSimplifiedContent(features: any[]): MapPopupContentRow[] {
    const contentRows = [];
    features.forEach(f => {
      const properties = f.properties;
      if (f.id.indexOf(ALLU_PREFIX) >= 0) {
        contentRows.push(
          this.createContentRowLink(properties.applicationId, properties.id, 'content-row-bold'),
          this.createContentRow(properties.applicant),
          this.createContentRow(`${properties.startTime} - ${properties.endTime}`));
      } else if (isWinkkiId(f.id)) {
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
}
