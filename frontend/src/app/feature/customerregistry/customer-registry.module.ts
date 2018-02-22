import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '../common/allu-common.module';
import {RouterModule} from '@angular/router';
import {customerRegistryRoutes} from './customer-registry.routing';
import {CustomerListComponent} from './customer-list.component';
import {CustomerComponent} from './customer/customer.component';
import {CustomerRegistryComponent} from './customer-registry.component';
import {InvoicingAddressComponent} from './invoicing-address/invoicing-address.component';
import {CustomerContactsComponent} from './contact/customer-contacts.component';
import {CustomerModalComponent} from './customer/customer-modal.component';
import {ContactModalComponent} from './contact/contact-modal.component';
import {CustomerInfoComponent} from './customer/customer-info.component';
import {MatPaginatorModule, MatSortModule, MatTableModule} from '@angular/material';

@NgModule({
  imports: [
    RouterModule.forChild(customerRegistryRoutes),
    FormsModule,
    ReactiveFormsModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    AlluCommonModule
  ],
  declarations: [
    CustomerRegistryComponent,
    CustomerListComponent,
    CustomerComponent,
    CustomerInfoComponent,
    InvoicingAddressComponent,
    CustomerContactsComponent,
    CustomerModalComponent,
    CustomerModalComponent,
    ContactModalComponent
  ],
  exports: [
    CustomerModalComponent,
    ContactModalComponent,
    InvoicingAddressComponent,
    CustomerInfoComponent
  ],
  entryComponents: [
    CustomerModalComponent,
    ContactModalComponent
  ]
})
export class CustomerRegistryModule {}
