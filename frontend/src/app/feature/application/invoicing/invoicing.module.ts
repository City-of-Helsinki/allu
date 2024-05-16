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
import {InvoicingCustomerEffects} from '@feature/application/invoicing/effects/invoicing-customer-effects';
import {InvoiceEffects} from '@feature/application/invoicing/effects/invoice-effects';
import {InvoiceListComponent} from '@feature/application/invoicing/invoice/invoice-list.component';
import {InvoiceComponent} from '@feature/application/invoicing/invoice/invoice.component';
import {InvoiceRowComponent} from '@feature/application/invoicing/invoice/invoice-row.component';
import {InvoiceGroupComponent} from '@feature/application/invoicing/invoice/invoice-group.component';
import {InvoicingPeriodSelectComponent} from '@feature/application/invoicing/invoicing-period/invoicing-period-select.component';
import {InvoicingPeriodEffects} from '@feature/application/invoicing/effects/invoicing-period-effects';
import {InvoicingPeriodService} from '@feature/application/invoicing/invoicing-period/invoicing-period.service';
import {DefaultTextModule} from '@feature/application/default-text/default-text.module';

@NgModule({
    imports: [
        FormsModule,
        ReactiveFormsModule,
        AlluCommonModule,
        CustomerRegistryModule,
        StoreModule.forFeature('invoicing', reducers),
        EffectsModule.forFeature([
            ChargeBasisEffects,
            InvoicingCustomerEffects,
            InvoiceEffects,
            InvoicingPeriodEffects
        ]),
        DefaultTextModule
    ],
    declarations: [
        InvoicingComponent,
        InvoicingInfoComponent,
        ChargeBasisComponent,
        ChargeBasisEntryComponent,
        InvoiceListComponent,
        InvoiceGroupComponent,
        InvoiceComponent,
        InvoiceRowComponent,
        InvoiceCommentsComponent,
        ChargeBasisEntryModalComponent,
        ChargeBasisDiscountComponent,
        ChargeBasisNegligenceFeeComponent,
        ChargeBasisFeeComponent,
        DepositModalComponent,
        InvoicingPeriodSelectComponent
    ],
    providers: [
        InvoiceService,
        DepositService,
        InvoicingPeriodService
    ]
})
export class InvoicingModule {}
