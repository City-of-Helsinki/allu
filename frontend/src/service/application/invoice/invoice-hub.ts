import {Injectable} from '@angular/core';
import '../../../rxjs-extensions.ts';
import {InvoiceService} from './invoice.service';
import {ChargeBasisEntry} from '../../../model/application/invoice/charge-basis-entry';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';

@Injectable()
export class InvoiceHub {
  private invoiceRows$ = new Subject<Array<ChargeBasisEntry>>();

  constructor(private invoiceService: InvoiceService) {
  }

  /**
   * Loads all invoice rows for given application
   */
  loadInvoiceRows = (applicationId: number) => this.invoiceService.getInvoiceRows(applicationId)
    .do(loaded => this.invoiceRows$.next(loaded));

  /**
   * Saves given rows for application
   * @returns all applications invoice rows
   */
  saveInvoiceRows = (applicationId: number, rows: Array<ChargeBasisEntry>) =>
    this.invoiceService.saveInvoiceRows(applicationId, rows)
      .do(savedRows => this.invoiceRows$.next(savedRows));

  /**
   * Observable to get latest saved invoice rows
   */
  get invoiceRows(): Observable<Array<ChargeBasisEntry>> {
    return this.invoiceRows$.asObservable();
  }
}
