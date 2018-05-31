import {NgModule} from '@angular/core';
import {EffectsModule} from '@ngrx/effects';
import {HistoryEffects} from './effects/history-effects';
import {HistoryComponent} from './history.component';
import {AlluCommonModule} from '../common/allu-common.module';

@NgModule({
  imports: [
    AlluCommonModule,
    EffectsModule.forFeature([HistoryEffects])
  ],
  declarations: [
    HistoryComponent
  ],
  providers: [],
  exports: [
    HistoryComponent
  ]
})
export class HistoryModule {}
