import {NgModule} from '@angular/core';
import {MatChipsModule, MatMenuModule} from '@angular/material';

import {AlluCommonModule} from '../../common/allu-common.module';
import {TagBarComponent} from './tagbar.component';

@NgModule({
  imports: [
    AlluCommonModule,
    MatChipsModule,
    MatMenuModule
  ],
  declarations: [
    TagBarComponent
  ],
  exports: [
    TagBarComponent
  ]
})
export class TagBarModule {}
