import {NgModule} from '@angular/core';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ConfigurationComponent} from '@feature/admin/configuration/configuration.component';
import {ConfigurationEntryComponent} from '@feature/admin/configuration/configuration-entry.component';
import {ConfigurationTextValueComponent} from '@feature/admin/configuration/configuration-text-value.component';
import {ConfigurationCalendarDateValueComponent} from '@feature/admin/configuration/configuration-calendar-date-value.component';
import {ConfigurationUserValueComponent} from '@feature/admin/configuration/configuration-user-value.component';
import {ConfigurationDateValueComponent} from '@feature/admin/configuration/configuration-date-value.component';
import {ConfigurationContactValueComponent} from '@feature/admin/configuration/configuration-contact-value.component';
import {StoreModule} from '@ngrx/store';
import {reducersProvider, reducersToken} from '@feature/admin/configuration/reducers';
import {EffectsModule} from '@ngrx/effects';
import {ConfigurationEffects} from '@feature/admin/configuration/effects/configuration-effects';
import {CustomerRegistryModule} from '@feature/customerregistry/customer-registry.module';

@NgModule({
  imports: [
    AlluCommonModule,
    FormsModule,
    ReactiveFormsModule,
    StoreModule.forFeature('configuration', reducersToken),
    EffectsModule.forFeature([ConfigurationEffects]),
    CustomerRegistryModule
  ],
  declarations: [
    ConfigurationComponent,
    ConfigurationEntryComponent,
    ConfigurationTextValueComponent,
    ConfigurationCalendarDateValueComponent,
    ConfigurationUserValueComponent,
    ConfigurationDateValueComponent,
    ConfigurationContactValueComponent
  ],
  providers: [
    reducersProvider
  ],
  exports: [
    ConfigurationComponent
  ]
})
export class ConfigurationModule {}
