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
import {ChargeBasisDiscountComponent} from './charge-basis/discount/charge-basis-discount.component';
import {ChargeBasisNegligenceFeeComponent} from './charge-basis/negligence-fee/charge-basis-negligence-fee.component';
import {ChargeBasisFeeComponent} from './charge-basis/charge-basis-fee/charge-basis-fee.component';
import {DepositModalComponent} from './deposit/deposit-modal.component';
import {DepositService} from '../../../service/application/deposit/deposit.service';

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
    ChargeBasisEntryModalComponent,
    ChargeBasisDiscountComponent,
    ChargeBasisNegligenceFeeComponent,
    ChargeBasisFeeComponent,
    DepositModalComponent
  ],
  providers: [
    InvoiceHub,
    InvoiceService,
    DepositService
  ],
  entryComponents: [
    ChargeBasisEntryModalComponent,
    DepositModalComponent
  ]
})
export class InvoicingModule {}
