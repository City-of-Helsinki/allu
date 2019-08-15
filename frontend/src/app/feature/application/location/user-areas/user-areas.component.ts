import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {Feature, GeometryObject} from 'geojson';
import {ArrayUtil} from '@util/array-util';

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

  private selectedAreas: number[] = [];

  constructor() {}

  areaSelected(area: Feature<GeometryObject>): void {
    this.selectedAreas = ArrayUtil.addUnique(this.selectedAreas, [area.properties.id]);
    this.areasSelected.emit([area]);
  }

  isSelected(userArea: Feature<GeometryObject>) {
    return this.selectedAreas.indexOf(userArea.properties.id) >= 0;
  }
}
