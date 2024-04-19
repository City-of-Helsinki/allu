import {NgModule} from '@angular/core';
import {MatLegacyChipsModule as MatChipsModule} from '@angular/material/legacy-chips';
import {MatLegacyMenuModule as MatMenuModule} from '@angular/material/legacy-menu';

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
