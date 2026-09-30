import {NgModule} from '@angular/core';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ApplicationIdentifierSelectComponent} from '@feature/application/identifier-select/application-identifier-select.component';
import {ApplicationIdentifierListComponent} from '@feature/application/identifier-select/application-identifier-list.component';
import {MatTableModule} from '@angular/material/table';
import {ApplicationIdentifiersComponent} from '@feature/application/identifier-select/application-identifiers.component';

@NgModule({
  imports: [
    AlluCommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatTableModule
  ],
  declarations: [
    ApplicationIdentifierSelectComponent,
    ApplicationIdentifierListComponent,
    ApplicationIdentifiersComponent
  ],
  exports: [
    ApplicationIdentifiersComponent
  ]
})
export class ApplicationIdentifierSelectModule {}
