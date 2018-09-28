import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AlluCommonModule} from '@feature/common/allu-common.module';
import {CustomerRegistryModule} from '@feature/customerregistry/customer-registry.module';
import {InvoicingComponent} from './invoicing.component';
import {InvoiceService} from '@service/application/invoice/invoice.service';
import {InvoiceCommentsComponent} from './invoice-comments.component';
import {ChargeBasisEntryModalComponent} from './charge-basis/charge-basis-entry-modal.component';
import {InvoicingInfoComponent} from './invoicing-info/invoicing-info.component';
import {ChargeBasisComponent} from './charge-basis/charge-basis.component';
import {ChargeBasisDiscountComponent} from './charge-basis/discount/charge-basis-discount.component';
import {ChargeBasisNegligenceFeeComponent} from './charge-basis/negligence-fee/charge-basis-negligence-fee.component';
import {ChargeBasisFeeComponent} from './charge-basis/charge-basis-fee/charge-basis-fee.component';
import {DepositModalComponent} from './deposit/deposit-modal.component';
import {DepositService} from '@service/application/deposit/deposit.service';
import {ChargeBasisEntryComponent} from '@feature/application/invoicing/charge-basis/charge-basis-entry.component';
import {StoreModule} from '@ngrx/store';
import {reducers} from './reducers';
import {EffectsModule} from '@ngrx/effects';
import {ChargeBasisEffects} from '@feature/application/invoicing/effects/charge-basis-effects';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    CustomerRegistryModule,
    StoreModule.forFeature('invoicing', reducers),
    EffectsModule.forFeature([
      ChargeBasisEffects
    ])
  ],
  declarations: [
    InvoicingComponent,
    InvoicingInfoComponent,
    ChargeBasisComponent,
    ChargeBasisEntryComponent,
    InvoiceCommentsComponent,
    ChargeBasisEntryModalComponent,
    ChargeBasisDiscountComponent,
    ChargeBasisNegligenceFeeComponent,
    ChargeBasisFeeComponent,
    DepositModalComponent
  ],
  providers: [
    InvoiceService,
    DepositService
  ],
  entryComponents: [
    ChargeBasisEntryModalComponent,
    DepositModalComponent
  ]
})
export class InvoicingModule {}
