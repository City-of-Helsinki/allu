import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {Feature, GeometryObject} from 'geojson';

@Component({
  selector: 'user-areas',
  templateUrl: './user-areas.component.html',
  styleUrls: [
    './user-areas.component.scss'
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserAreasComponent {

  @Input() loading = false;
  @Input() userAreas: Feature<GeometryObject>[] = [];

  @Output() areasSelected: EventEmitter<Feature<GeometryObject>[]> = new EventEmitter<Feature<GeometryObject>[]>();

  constructor() {}

  areaSelected(area: Feature<GeometryObject>): void {
    this.areasSelected.emit([area]);
  }
}
