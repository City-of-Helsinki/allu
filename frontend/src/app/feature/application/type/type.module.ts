import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {MatLegacyCardModule as MatCardModule} from '@angular/material/legacy-card';
import {MatLegacySlideToggleModule as MatSlideToggleModule} from '@angular/material/legacy-slide-toggle';

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
