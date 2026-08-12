import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';

import {AlluCommonModule} from '../../common/allu-common.module';
import {TypeComponent} from './type.component';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    AlluCommonModule,
    MatCardModule,
    MatSlideToggleModule
  ],
  declarations: [
    TypeComponent
  ],
  exports: [
    TypeComponent
  ]
})
export class TypeModule {}
