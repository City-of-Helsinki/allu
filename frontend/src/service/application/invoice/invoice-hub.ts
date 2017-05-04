import {Injectable} from '@angular/core';
import '../../../rxjs-extensions.ts';
import {InvoiceService} from './invoice.service';
import {InvoiceRow} from '../../../model/application/invoice/invoice-row';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';

@Injectable()
export class InvoiceHub {
  private invoiceRows$ = new Subject<Array<InvoiceRow>>();

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
  saveInvoiceRows = (applicationId: number, rows: Array<InvoiceRow>) =>
    this.invoiceService.saveInvoiceRows(applicationId, rows)
      .do(savedRows => this.invoiceRows$.next(savedRows));

  /**
   * Observable to get latest saved invoice rows
   */
  get invoiceRows(): Observable<Array<InvoiceRow>> {
    return this.invoiceRows$.asObservable();
  }
}
