import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AlluCommonModule} from '../../common/allu-common.module';
import {CustomerRegistryModule} from '../../customerregistry/customer-registry.module';
import {InvoicingComponent} from './invoicing.component';
import {InvoiceService} from '../../../service/application/invoice/invoice.service';
import {InvoiceCommentsComponent} from './invoice-comments.component';
import {ChargeBasisEntryModalComponent} from './charge-basis/charge-basis-entry-modal.component';
import {InvoicingInfoComponent} from './invoicing-info/invoicing-info.component';
import {ChargeBasisComponent} from './charge-basis/charge-basis.component';
import {InvoiceHub} from '../../../service/application/invoice/invoice-hub';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    CustomerRegistryModule
  ],
  declarations: [
    InvoicingComponent,
    InvoicingInfoComponent,
    ChargeBasisComponent,
    InvoiceCommentsComponent,
    ChargeBasisEntryModalComponent
  ],
  providers: [
    InvoiceHub,
    InvoiceService
  ],
  entryComponents: [
    ChargeBasisEntryModalComponent
  ]
})
export class InvoicingModule {}
