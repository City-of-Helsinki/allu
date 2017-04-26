import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AlluCommonModule} from '../../common/allu-common.module';
import {CustomerRegistryModule} from '../../customerregistry/customer-registry.module';
import {InvoicingComponent} from './invoicing.component';
import {InvoiceRowsComponent} from './invoice-rows.component';
import {InvoiceHub} from '../../../service/application/invoice/invoice-hub';
import {InvoiceService} from '../../../service/application/invoice/invoice.service';
import {InvoiceCommentsComponent} from './invoice-comments.component';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    CustomerRegistryModule
  ],
  declarations: [
    InvoicingComponent,
    InvoiceRowsComponent,
    InvoiceCommentsComponent
  ],
  providers: [
    InvoiceHub,
    InvoiceService
  ]
})
export class InvoicingModule {}
