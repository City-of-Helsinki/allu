import {Injectable} from '@angular/core';
import {Location} from '../../model/common/location';

@Injectable()
export class LocationState {
  public location = new Location();
  public startDate: Date;
  public endDate: Date;

  public clear() {
    this.location = new Location();
    this.startDate = undefined;
    this.endDate = undefined;
  }
}
