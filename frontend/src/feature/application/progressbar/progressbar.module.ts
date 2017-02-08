import {NgModule} from '@angular/core';
import {AlluCommonModule} from '../../common/allu-common.module';
import {ProgressbarComponent} from './progressbar.component';
import {MdProgressBarModule} from '@angular/material';

@NgModule({
  imports: [
    AlluCommonModule,
    MdProgressBarModule
  ],
  declarations: [
    ProgressbarComponent
  ],
  exports: [
    ProgressbarComponent
  ]
})
export class ProgressBarModule {}
