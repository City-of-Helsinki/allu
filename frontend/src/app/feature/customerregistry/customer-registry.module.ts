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
import {MatPaginatorModule, MatSortModule, MatTableModule} from '@angular/material';
import {InformationRequestModule} from '../information-request/information-request.module';
import {StoreModule} from '@ngrx/store';
import {EffectsModule} from '@ngrx/effects';
import {CustomerSearchEffects} from './effects/customer-search-effects';
import {ContactSearchEffects} from '@feature/customerregistry/effects/contact-search-effects';
import {reducersProvider, reducersToken} from '@feature/customerregistry/reducers';

@NgModule({
  imports: [
    RouterModule.forChild(customerRegistryRoutes),
    FormsModule,
    StoreModule.forFeature('customer', reducersToken),
    EffectsModule.forFeature([
      CustomerSearchEffects,
      ContactSearchEffects
    ]),
    ReactiveFormsModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    AlluCommonModule,
    InformationRequestModule
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
  ],
  exports: [
    CustomerModalComponent,
    ContactModalComponent,
    CustomerInfoComponent
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
