import {NgModule} from '@angular/core';
import {AlluCommonModule} from '../../common/allu-common.module';
import {ProgressbarComponent} from './progressbar.component';
import {MatProgressBarModule} from '@angular/material';
import {RouterModule} from '@angular/router';

@NgModule({
  imports: [
    AlluCommonModule,
    MatProgressBarModule,
    RouterModule
  ],
  declarations: [
    ProgressbarComponent
  ],
  exports: [
    ProgressbarComponent
  ]
})
export class ProgressBarModule {}
