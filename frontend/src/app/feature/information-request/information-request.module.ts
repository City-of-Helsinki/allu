import {NgModule} from '@angular/core';
import {EffectsModule} from '@ngrx/effects';
import {InformationRequestEffects} from './effects/information-request-effects';
import {InformationRequestService} from '@service/application/information-request.service';
import {MatDialogModule} from '@angular/material/dialog';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '../common/allu-common.module';
import {InformationAcceptanceModalComponent} from './acceptance/information-acceptance-modal.component';
import {CustomerInfoAcceptanceComponent} from './acceptance/customer/customer-info-acceptance.component';
import {FieldValueComponent} from './acceptance/field-select/field-value.component';
import {StoreModule} from '@ngrx/store';
import {reducers} from './reducers';
import {KindAcceptanceComponent} from './acceptance/kind/kind-acceptance.component';
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
import {InformationRequestFieldsComponent} from '@feature/information-request/request/display/information-request-fields.component';
import {CustomerRegistryModule} from '@feature/customerregistry/customer-registry.module';
import {InformationAcceptanceEntryComponent} from '@feature/information-request/acceptance/information-acceptance-entry.component';
import {RouterModule} from '@angular/router';
import {InformationRequestSummaryModule} from '@feature/information-request/summary/information-request-summary.module';
import {InformationRequestEntryComponent} from '@feature/information-request/acceptance/information-request-entry.component';
import {InformationAcceptanceResolve} from '@feature/information-request/acceptance/information-acceptance-resolve';

@NgModule({
    imports: [
        StoreModule.forFeature('informationRequest', reducers),
        EffectsModule.forFeature([InformationRequestEffects]),
        FormsModule,
        ReactiveFormsModule,
        MatDialogModule,
        AlluCommonModule,
        MapModule,
        CustomerRegistryModule,
        RouterModule.forChild([]),
        InformationRequestSummaryModule
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
        InformationRequestFieldsComponent,
        InformationAcceptanceEntryComponent,
        InformationRequestEntryComponent,
    ],
    providers: [
        InformationRequestService,
        InformationRequestResultService,
        InformationAcceptanceResolve
    ],
    exports: [
        InformationAcceptanceModalComponent,
        InformationAcceptanceEntryComponent,
        InformationRequestEntryComponent
    ]
})
export class InformationRequestModule {}
