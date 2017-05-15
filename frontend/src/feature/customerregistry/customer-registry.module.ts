import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '../common/allu-common.module';
import {RouterModule} from '@angular/router';
import {customerRegistryRoutes} from './customer-registry.routing';
import {CustomerListComponent} from './customer-list.component';
import {CustomerComponent} from './applicant/customer.component';
import {CustomerRegistryComponent} from './customer-registry.component';
import {InvoicingAddressComponent} from './invoicing-address/invoicing-address.component';
import {CustomerContactsComponent} from './contact/customer-contacts.component';
import {ApplicantModalComponent} from './applicant/applicant-modal.component';
import {ContactModalComponent} from './contact/contact-modal.component';
import {CustomerInfoComponent} from './applicant/customer-info.component';

@NgModule({
  imports: [
    RouterModule.forChild(customerRegistryRoutes),
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule
  ],
  declarations: [
    CustomerRegistryComponent,
    CustomerListComponent,
    CustomerComponent,
    CustomerInfoComponent,
    InvoicingAddressComponent,
    CustomerContactsComponent,
    ApplicantModalComponent,
    ApplicantModalComponent,
    ContactModalComponent
  ],
  exports: [
    ApplicantModalComponent,
    ContactModalComponent,
    InvoicingAddressComponent,
    CustomerInfoComponent
  ],
  entryComponents: [
    ApplicantModalComponent,
    ContactModalComponent
  ]
})
export class CustomerRegistryModule {}
