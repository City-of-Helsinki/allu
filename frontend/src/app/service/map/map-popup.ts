import * as L from 'leaflet';
import {ALLU_PREFIX, isWinkkiId} from './map-layer-id';
import {findTranslation} from '../../util/translations';
import {Router} from '@angular/router';

export class MapPopup {

  static create(features: any[], router: Router): HTMLElement {
    if (features.length) {
      const header = this.createHeader(features);
      const content = this.createContent(features, router);

      const popup = L.DomUtil.create('div', 'popup-wrapper');
      popup.appendChild(header);
      popup.appendChild(content);
      return popup;
    } else  {
      throw Error('Popup requires at least one feature');
    }
  }

  private static createHeader(features: any[]): HTMLElement {
    let header: HTMLElement;
    if (features.length > 1) {
      header = L.DomUtil.create('h1', 'popup-header');
      header.innerHTML = findTranslation('map.popup.titleMultipleFeatures', {count: String(features.length)});
    } else {
      header = this.createSingeFeatureHeader(features[0]);
    }
    return header;
  }

  private static createSingeFeatureHeader(feature: any): HTMLElement {
    const header = L.DomUtil.create('h1', 'popup-header');
    const properties = feature.properties;

    if (feature.id.indexOf(ALLU_PREFIX) >= 0) {
      header.innerHTML = properties.applicationId;
    } else if (isWinkkiId(feature.id)) {
      header.innerHTML = properties.licence_identifier;
    } else {
      throw new Error(`Unknown feature id ${feature.id}`);
    }

    return header;
  }

  private static createContent(features: any[], router: Router): HTMLElement {
    const contentList = L.DomUtil.create('ul', 'popup-content');
    const rows = features.length > 1
      ? this.createSimplifiedContent(features, router)
      : this.createDetailedContent(features[0], router);

    rows.forEach(row => contentList.appendChild(row));
    return contentList;
  }

  private static createDetailedContent(feature: any, router: Router): HTMLElement[] {
    const properties = feature.properties;
    let contentRows = [];

    if (feature.id.indexOf(ALLU_PREFIX) >= 0) {
      contentRows = [
        this.createContentRowLink(this.bold(properties.name), properties.id, router),
        this.createContentRow(`${properties.startTime} - ${properties.endTime}`)
      ];
    } else if (isWinkkiId(feature.id)) {
      contentRows = [
        this.createContentRow(this.bold(properties.event_description)),
        this.createContentRow(`${properties.lic_startdate_txt} - ${properties.lic_enddate_txt}`)
      ];
    } else {
      throw new Error(`Unknown feature id ${feature.id}`);
    }

    return contentRows;
  }

  private static createSimplifiedContent(features: any[], router: Router): HTMLElement[] {
    const contentRows = [];
    features.forEach(f => {
      const properties = f.properties;
      if (f.id.indexOf(ALLU_PREFIX) >= 0) {
        contentRows.push(
          this.createContentRowLink(this.bold(properties.applicationId), properties.id, router),
          this.createContentRow(`${properties.startTime} - ${properties.endTime}`));
      } else if (isWinkkiId(f.id)) {
        contentRows.push(
          this.createContentRow(this.bold(properties.licence_identifier)),
          this.createContentRow(`${properties.lic_startdate_txt} - ${properties.lic_enddate_txt}`));
      }
    });
    return contentRows;
  }

  private static bold(content: string): string {
    return `<b>${content}</b>`;
  }

  private static createContentRow(content: string): HTMLElement {
    const row = L.DomUtil.create('li', undefined);
    row.innerHTML = content;
    return row;
  }

  private static createContentRowLink(content: string,  id: number, router: Router): HTMLElement {
    const row = this.createContentRow(content);
    row.onclick = (event: MouseEvent) => router.navigate(['applications', id, 'summary']);
    row.className = 'clickable';
    return row;
  }
}
