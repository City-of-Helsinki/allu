import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/index';

@Injectable()
export class InformationAcceptanceModalEvents {
  private _modalOpen: Subject<boolean> = new Subject<boolean>();

  public open(): void {
    this._modalOpen.next(true);
  }

  get isOpen$() {
    return this._modalOpen.asObservable();
  }
}
