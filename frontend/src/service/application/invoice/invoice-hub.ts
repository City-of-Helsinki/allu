import {Injectable} from '@angular/core';
import '../../../rxjs-extensions.ts';
import {InvoiceService} from './invoice.service';
import {ChargeBasisEntry} from '../../../model/application/invoice/charge-basis-entry';
import {Observable} from 'rxjs/Observable';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@Injectable()
export class InvoiceHub {
  private chargeBasisEntries$ = new BehaviorSubject<Array<ChargeBasisEntry>>([]);

  constructor(private invoiceService: InvoiceService) {
  }

  /**
   * Loads all charge basis entries for given application
   */
  loadChargeBasisEntries = (applicationId: number) => this.invoiceService.getChargeBasisEntries(applicationId)
    .do(loaded => this.chargeBasisEntries$.next(loaded));

  /**
   * Saves given charge basis entries for application
   * @returns all applications charge basis entries
   */
  saveChargeBasisEntries = (applicationId: number, rows: Array<ChargeBasisEntry>) =>
    this.invoiceService.saveChargeBasisEntries(applicationId, rows)
      .do(savedRows => this.chargeBasisEntries$.next(savedRows));

  /**
   * Observable to get latest saved charge basis entries
   */
  get chargeBasisEntries(): Observable<Array<ChargeBasisEntry>> {
    return this.chargeBasisEntries$.asObservable();
  }
}
