import {NgModule} from '@angular/core';
import {AlluCommonModule} from '../../common/allu-common.module';
import {ProgressbarComponent} from './progressbar.component';
import {MatMenuModule} from '@angular/material/menu';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';

@NgModule({
  imports: [
    AlluCommonModule,
    MatProgressBarModule,
    MatMenuModule,
    FormsModule,
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
