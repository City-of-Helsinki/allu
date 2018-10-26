import {NgModule} from '@angular/core';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ConfigurationComponent} from '@feature/admin/configuration/configuration.component';
import {ConfigurationEntryComponent} from '@feature/admin/configuration/configuration-entry.component';
import {ConfigurationTextValueComponent} from '@feature/admin/configuration/configuration-text-value.component';
import {ConfigurationCalendarDateValueComponent} from '@feature/admin/configuration/configuration-calendar-date-value.component';

@NgModule({
  imports: [
    AlluCommonModule,
    FormsModule,
    ReactiveFormsModule
  ],
  declarations: [
    ConfigurationComponent,
    ConfigurationEntryComponent,
    ConfigurationTextValueComponent,
    ConfigurationCalendarDateValueComponent
  ],
  providers: [],
  exports: [
    ConfigurationComponent
  ]
})
export class ConfigurationModule {}
