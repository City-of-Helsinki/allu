import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '../common/allu-common.module';
import {RouterModule} from '@angular/router';
import {customerRegistryRoutes} from './customer-registry.routing';
import {CustomerListComponent} from './customer-list.component';
import {CustomerComponent} from './customer/customer.component';
import {CustomerRegistryComponent} from './customer-registry.component';
import {CustomerContactsComponent} from './contact/customer-contacts.component';
import {CustomerModalComponent} from './customer/customer-modal.component';
import {ContactModalComponent} from './contact/contact-modal.component';
import {CustomerInfoComponent} from './customer/customer-info.component';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table';
import {StoreModule} from '@ngrx/store';
import {EffectsModule} from '@ngrx/effects';
import {CustomerSearchEffects} from './effects/customer-search-effects';
import {ContactSearchEffects} from '@feature/customerregistry/effects/contact-search-effects';
import {reducersProvider, reducersToken} from '@feature/customerregistry/reducers';
import {CustomerOptionContentComponent} from '@feature/customerregistry/customer/customer-option-content.component';
import {ContactEffects} from '@feature/customerregistry/effects/contact-effects';

@NgModule({
  imports: [
    RouterModule.forChild(customerRegistryRoutes),
    FormsModule,
    StoreModule.forFeature('customer', reducersToken),
    EffectsModule.forFeature([
      CustomerSearchEffects,
      ContactSearchEffects,
      ContactEffects
    ]),
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
    CustomerContactsComponent,
    CustomerModalComponent,
    CustomerModalComponent,
    ContactModalComponent,
    CustomerOptionContentComponent
  ],
  exports: [
    CustomerModalComponent,
    ContactModalComponent,
    CustomerInfoComponent,
    CustomerOptionContentComponent
  ],
  providers: [
    reducersProvider
  ],
  entryComponents: [
    CustomerModalComponent,
    ContactModalComponent,
  ]
})
export class CustomerRegistryModule {}
