import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/index';
import {DistributionEntry} from '@model/common/distribution-entry';

@Injectable()
export class DistributionListEvents {
  private _addToList: Subject<DistributionEntry> = new Subject<DistributionEntry>();

  public add(entry: DistributionEntry): void {
    this._addToList.next(entry);
  }

  get distributionList$() {
    return this._addToList.asObservable();
  }
}
