import {NgModule} from '@angular/core';
import {AlluCommonModule} from '../../common/allu-common.module';
import {ProgressbarComponent} from './progressbar.component';
import {MatProgressBarModule} from '@angular/material';

@NgModule({
  imports: [
    AlluCommonModule,
    MatProgressBarModule
  ],
  declarations: [
    ProgressbarComponent
  ],
  exports: [
    ProgressbarComponent
  ]
})
export class ProgressBarModule {}
