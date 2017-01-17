import {NgModule} from '@angular/core';
import {MdChipsModule, MdMenuModule} from '@angular/material';

import {AlluCommonModule} from '../../common/allu-common.module';
import {TagBarComponent} from './tagbar.component';

@NgModule({
  imports: [
    AlluCommonModule,
    MdChipsModule,
    MdMenuModule
  ],
  declarations: [
    TagBarComponent
  ],
  exports: [
    TagBarComponent
  ]
})
export class TagBarModule {}
