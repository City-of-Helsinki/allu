import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {MdCardModule} from '@angular/material';

import {AlluCommonModule} from '../../common/allu-common.module';
import {TypeComponent} from './type.component';

@NgModule({
  imports: [
    FormsModule,
    RouterModule,
    AlluCommonModule,
    MdCardModule
  ],
  declarations: [
    TypeComponent
  ],
  exports: [
    TypeComponent
  ]
})
export class TypeModule {}
