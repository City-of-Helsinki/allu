import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {MatCardModule} from '@angular/material';

import {AlluCommonModule} from '../../common/allu-common.module';
import {TypeComponent} from './type.component';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    AlluCommonModule,
    MatCardModule
  ],
  declarations: [
    TypeComponent
  ],
  exports: [
    TypeComponent
  ]
})
export class TypeModule {}
