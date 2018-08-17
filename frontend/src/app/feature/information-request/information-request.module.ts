import {NgModule} from '@angular/core';
import {EffectsModule} from '@ngrx/effects';
import {InformationRequestEffects} from './effects/information-request-effects';
import {InformationRequestService} from '../../service/application/information-request.service';
import {MatDialogModule} from '@angular/material';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '../common/allu-common.module';
import {InformationAcceptanceModalComponent} from './acceptance/information-acceptance-modal.component';
import {FieldAcceptanceComponent} from './acceptance/field-acceptance.component';
import {FieldGroupAcceptanceComponent} from './acceptance/field-group-acceptance.component';
import {CustomerInfoAcceptanceComponent} from './acceptance/customer/customer-info-acceptance.component';
import {FieldValueComponent} from './acceptance/field-value.component';
import {StoreModule} from '@ngrx/store';
import {reducers} from './reducers';
import {KindAcceptanceComponent} from './acceptance/kind-acceptance.component';
import {InformationAcceptanceModalEvents} from './acceptance/information-acceptance-modal-events';
import {
  CustomerWithContactsAcceptanceComponent
} from '@feature/information-request/acceptance/customer-with-contacts-acceptance.component';
import {ContactsAcceptanceComponent} from '@feature/information-request/acceptance/contact/contacts-acceptance-component';
import {ContactAcceptanceComponent} from '@feature/information-request/acceptance/contact/contact-acceptance.component';
import {ContactInfoAcceptanceComponent} from '@feature/information-request/acceptance/contact/contact-info-acceptance.component';
import {FieldDisplayComponent} from '@feature/information-request/acceptance/field-display.component';
import {
  InvoiceCustomerAcceptanceComponent
} from '@feature/information-request/acceptance/invoice-customer/invoice-customer-acceptance.component';
import {ApplicantAcceptanceComponent} from '@feature/information-request/acceptance/customer/applicant-acceptance.component';
import {OtherAcceptanceComponent} from '@feature/information-request/acceptance/other/other-acceptance.component';
import {OtherInfoAcceptanceComponent} from '@feature/information-request/acceptance/other/other-info-acceptance.component';
import {InformationRequestResultService} from '@feature/information-request/acceptance/result/information-request-result.service';
import { InformationRequestModalComponent } from './request/information-request-modal.component';
import { RequestFieldComponent } from './request/request-field/request-field.component';

@NgModule({
  imports: [
    StoreModule.forFeature('informationRequest', reducers),
    EffectsModule.forFeature([InformationRequestEffects]),
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    AlluCommonModule
  ],
  declarations: [
    InformationAcceptanceModalComponent,
    FieldValueComponent,
    FieldDisplayComponent,
    FieldAcceptanceComponent,
    FieldGroupAcceptanceComponent,
    CustomerWithContactsAcceptanceComponent,
    ApplicantAcceptanceComponent,
    CustomerInfoAcceptanceComponent,
    ContactsAcceptanceComponent,
    ContactAcceptanceComponent,
    ContactInfoAcceptanceComponent,
    KindAcceptanceComponent,
    InvoiceCustomerAcceptanceComponent,
    OtherAcceptanceComponent,
    OtherInfoAcceptanceComponent,
    InformationRequestModalComponent,
    RequestFieldComponent
  ],
  providers: [
    InformationRequestService,
    InformationAcceptanceModalEvents,
    InformationRequestResultService
  ],
  exports: [
    InformationAcceptanceModalComponent
  ],
  entryComponents: [
    InformationAcceptanceModalComponent,
    InformationRequestModalComponent
  ]
})
export class InformationRequestModule {}
