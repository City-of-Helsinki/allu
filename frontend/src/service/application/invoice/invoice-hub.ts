import {Injectable} from '@angular/core';
import '../../../rxjs-extensions.ts';
import {InvoiceService} from './invoice.service';

@Injectable()
export class InvoiceHub {
  constructor(private invoiceService: InvoiceService) {
  }

  /**
   * Get all invoice rows for given application
   */
  getInvoiceRows = (applicationId: number) => this.invoiceService.getInvoiceRows(applicationId);
}
