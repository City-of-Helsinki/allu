import {NgModule} from '@angular/core';
import {AlluCommonModule} from '../common/allu-common.module';
import {ProgressbarComponent} from './progressbar.component';

@NgModule({
  imports: [
    AlluCommonModule
  ],
  declarations: [
    ProgressbarComponent
  ],
  exports: [
    ProgressbarComponent
  ]
})
export class ProgressBarModule {}
