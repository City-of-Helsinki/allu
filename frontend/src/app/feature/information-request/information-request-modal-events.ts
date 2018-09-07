import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs/index';

@Injectable()
export class InformationRequestModalEvents {
  private _requestModalOpen: Subject<boolean> = new Subject<boolean>();
  private _acceptanceModalOpen: Subject<boolean> = new Subject<boolean>();

  public openRequest(): void {
    this._requestModalOpen.next(true);
  }

  public openAcceptance(): void {
    this._acceptanceModalOpen.next(true);
  }

  get isRequestOpen$(): Observable<boolean> {
    return this._requestModalOpen.asObservable();
  }

  get isAcceptanceOpen$() {
    return this._acceptanceModalOpen.asObservable();
  }
}
