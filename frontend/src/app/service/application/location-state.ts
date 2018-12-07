import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {NumberUtil} from '../../util/number.util';
import {Location} from '../../model/common/location';
import {MapStore} from '../map/map-store';
import {Some} from '../../util/option';

@Injectable()
export class LocationState {
  private locations$ = new BehaviorSubject<Array<Location>>([]);
  private _editIndex: number;

  constructor(private mapStore: MapStore) {
    this.locations.subscribe(locations => this.notifyOnChange(locations));
  }

  initLocations(locations: Array<Location>): void {
    this._editIndex = 0;
    this.locations$.next(locations);
  }

  get locations(): Observable<Array<Location>> {
    return this.locations$.asObservable();
  }

  get locationsSnapshot(): Array<Location> {
    return this.locations$.getValue();
  }

  get editIndex() {
    return this._editIndex;
  }

  storeLocation(location: Location): void {
    if (this.validLocation(location)) {
      const current = this.locations$.getValue();
      let next;
      if (NumberUtil.isDefined(this._editIndex)) {
        current.splice(this.editIndex, 1, location);
        next = current;
      } else {
        next = current.concat(location);
      }

      this.clearEdited();
      this.locations$.next(next);
    }
  }

  removeLocation(index: number): void {
    const current = this.locations$.getValue();
    current.splice(index, 1);
    this.locations$.next(current);
    this.clearEdited();
  }

  editLocation(index: number): void {
    this._editIndex = index;
    this.locations$.next(this.locations$.getValue().slice());
  }

  cancelEditing(): void {
    this.clearEdited();
    this.locations$.next(this.locations$.getValue().slice());
  }

  private clearEdited(): void {
    this._editIndex = undefined;
    this.mapStore.editedLocationChange(undefined);
  }

  private notifyOnChange(locations: Array<Location>): void {
    const otherLocations = locations.slice(); // Copy array elements
    const editedLocation = Some(this._editIndex)
      .map(index => otherLocations.splice(index, 1)[0]) // Splice returns removed value
      .orElse(undefined);

    this.mapStore.editedLocationChange(editedLocation);
    this.mapStore.locationsToDrawChange(otherLocations);
  }

  private validLocation(location: Location): boolean {
    return !!location.startTime && !!location.endTime && !!location.geometry;
  }
}
