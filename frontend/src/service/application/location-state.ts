import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import {NumberUtil} from '../../util/number.util';
import {Location} from '../../model/common/location';
import {MapHub} from '../map/map-hub';
import {Some} from '../../util/option';

@Injectable()
export class LocationState {
  private locations$ = new BehaviorSubject<Array<Location>>([]);
  private _editIndex: number;

  constructor(private mapHub: MapHub) {}

  initLocations(locations: Array<Location>): void {
    this.locations$.next(locations);
    this.notifyOnChange();
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
      let current = this.locations$.getValue();
      let next;
      if (NumberUtil.isDefined(this._editIndex)) {
        current.splice(this.editIndex, 1, location);
        next = current;
      } else {
        next = current.concat(location);
      }
      this.locations$.next(next);
      this._editIndex = undefined;
      this.notifyOnChange();
    }
  }

  removeLocation(index: number): void {
    let current = this.locations$.getValue();
    current.splice(index, 1);
    this.locations$.next(current);
    this.notifyOnChange();
  }

  editLocation(index: number): void {
    this._editIndex = index;
    this.notifyOnChange();
  }

  cancelEditing(): void {
    this._editIndex = undefined;
    this.mapHub.editLocation(undefined);
    this.notifyOnChange();
  }

  notifyOnChange(): void {
    let otherLocations = this.locations$.getValue().slice(); // Copy array elements
    let editedLocation = Some(this._editIndex)
      .map(index => otherLocations.splice(index, 1)[0]) // Splice returns removed value
      .orElse(undefined);

    this.mapHub.editLocation(editedLocation);
    this.mapHub.drawLocations(otherLocations);
  }

  private validLocation(location: Location): boolean {
    return !!location.startTime && !!location.endTime && !!location.geometry;
  }
}
