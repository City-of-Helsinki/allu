import {NgModule} from '@angular/core';
import {EffectsModule} from '@ngrx/effects';
import {InformationRequestEffects} from './effects/information-request-effects';
import {InformationRequestService} from '@service/application/information-request.service';
import {MatDialogModule} from '@angular/material';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '../common/allu-common.module';
import {InformationAcceptanceModalComponent} from './acceptance/information-acceptance-modal.component';
import {CustomerInfoAcceptanceComponent} from './acceptance/customer/customer-info-acceptance.component';
import {FieldValueComponent} from './acceptance/field-select/field-value.component';
import {StoreModule} from '@ngrx/store';
import {reducers} from './reducers';
import {KindAcceptanceComponent} from './acceptance/kind/kind-acceptance.component';
import {InformationRequestModalEvents} from './information-request-modal-events';
import {CustomerWithContactsAcceptanceComponent} from '@feature/information-request/acceptance/customer-with-contacts-acceptance.component';
import {ContactsAcceptanceComponent} from '@feature/information-request/acceptance/contact/contacts-acceptance-component';
import {ContactAcceptanceComponent} from '@feature/information-request/acceptance/contact/contact-acceptance.component';
import {OtherAcceptanceComponent} from '@feature/information-request/acceptance/other/other-acceptance.component';
import {OtherInfoAcceptanceComponent} from '@feature/information-request/acceptance/other/other-info-acceptance.component';
import {InformationRequestResultService} from '@feature/information-request/acceptance/result/information-request-result.service';
import {InformationRequestModalComponent} from './request/information-request-modal.component';
import {RequestFieldComponent} from './request/request-field/request-field.component';
import {FieldSelectComponent} from '@feature/information-request/acceptance/field-select/field-select.component';
import {ContactInfoAcceptanceComponent} from '@feature/information-request/acceptance/contact/contact-info-acceptance.component';
import {CustomerModalComponent} from '@feature/information-request/acceptance/customer/customer-modal.component';
import {ContactModalComponent} from '@feature/information-request/acceptance/contact/contact-modal.component';
import {CustomerAcceptanceComponent} from '@feature/information-request/acceptance/customer/customer-acceptance.component';
import {LocationsAcceptanceComponent} from '@feature/information-request/acceptance/location/locations-acceptance.component';
import {LocationAcceptanceComponent} from '@feature/information-request/acceptance/location/location-acceptance.component';
import {LocationInfoAcceptanceComponent} from '@feature/information-request/acceptance/location/location-info-acceptance.component';
import {MapModule} from '@feature/map/map.module';
import {AttachmentsAcceptanceComponent} from '@feature/information-request/acceptance/attachment/attachments-acceptance.component';
import {InformationRequestSummaryComponent} from '@feature/information-request/request/display/information-request-summary.component';

@NgModule({
  imports: [
    StoreModule.forFeature('informationRequest', reducers),
    EffectsModule.forFeature([InformationRequestEffects]),
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    AlluCommonModule,
    MapModule
  ],
  declarations: [
    InformationAcceptanceModalComponent,
    FieldValueComponent,
    CustomerWithContactsAcceptanceComponent,
    CustomerAcceptanceComponent,
    CustomerInfoAcceptanceComponent,
    ContactsAcceptanceComponent,
    ContactAcceptanceComponent,
    ContactInfoAcceptanceComponent,
    KindAcceptanceComponent,
    OtherAcceptanceComponent,
    OtherInfoAcceptanceComponent,
    InformationRequestModalComponent,
    RequestFieldComponent,
    FieldSelectComponent,
    CustomerModalComponent,
    ContactModalComponent,
    LocationsAcceptanceComponent,
    LocationAcceptanceComponent,
    LocationInfoAcceptanceComponent,
    AttachmentsAcceptanceComponent,
    InformationRequestSummaryComponent
  ],
  providers: [
    InformationRequestService,
    InformationRequestModalEvents,
    InformationRequestResultService
  ],
  exports: [
    InformationAcceptanceModalComponent
  ],
  entryComponents: [
    InformationAcceptanceModalComponent,
    InformationRequestModalComponent,
    CustomerModalComponent,
    ContactModalComponent
  ]
})
export class InformationRequestModule {}
