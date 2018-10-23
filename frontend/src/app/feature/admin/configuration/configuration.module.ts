import {NgModule} from '@angular/core';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ConfigurationComponent} from '@feature/admin/configuration/configuration.component';
import {ConfigurationEntryComponent} from '@feature/admin/configuration/configuration-entry.component';
import {ConfigurationTextValueComponent} from '@feature/admin/configuration/configuration-text-value.component';

@NgModule({
  imports: [
    AlluCommonModule,
    FormsModule,
    ReactiveFormsModule
  ],
  declarations: [
    ConfigurationComponent,
    ConfigurationEntryComponent,
    ConfigurationTextValueComponent
  ],
  providers: [],
  exports: [
    ConfigurationComponent
  ]
})
export class ConfigurationModule {}
