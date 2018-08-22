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
import {CustomerAcceptanceComponent} from './acceptance/customer/customer-acceptance.component';
import {CustomerWithContactsAcceptanceComponent} from '@feature/information-request/acceptance/customer-with-contacts-acceptance.component';
import {ContactsAcceptanceComponent} from '@feature/information-request/acceptance/contact/contacts-acceptance-component';
import {ContactAcceptanceComponent} from '@feature/information-request/acceptance/contact/contact-acceptance.component';
import {ContactInfoAcceptanceComponent} from '@feature/information-request/acceptance/contact/contact-info-acceptance.component';
import {FieldDisplayComponent} from '@feature/information-request/acceptance/field-display.component';

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
    CustomerAcceptanceComponent,
    CustomerInfoAcceptanceComponent,
    ContactsAcceptanceComponent,
    ContactAcceptanceComponent,
    ContactInfoAcceptanceComponent,
    KindAcceptanceComponent
  ],
  providers: [
    InformationRequestService,
    InformationAcceptanceModalEvents
  ],
  exports: [
    InformationAcceptanceModalComponent
  ],
  entryComponents: [
    InformationAcceptanceModalComponent
  ]
})
export class InformationRequestModule {}
