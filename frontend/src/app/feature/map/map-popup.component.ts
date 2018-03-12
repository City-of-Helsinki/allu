import {Component, Input} from '@angular/core';

@Component({
  selector: 'map-popup',
  templateUrl: './map-popup.component.html',
  styleUrls: ['./map-popup.component.scss']
})
export class MapPopupComponent {
  @Input() header;
  @Input() contentRows: MapPopupContentRow[];
}

export interface MapPopupContentRow {
  content: string;
  link?: string;
  class?: string;
}
