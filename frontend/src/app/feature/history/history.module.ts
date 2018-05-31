import {NgModule} from '@angular/core';
import {EffectsModule} from '@ngrx/effects';
import {HistoryEffects} from './effects/history-effects';
import {HistoryComponent} from './history.component';
import {AlluCommonModule} from '../common/allu-common.module';
import {HistoryItemComponent} from './history-item.component';

@NgModule({
  imports: [
    AlluCommonModule,
    EffectsModule.forFeature([HistoryEffects])
  ],
  declarations: [
    HistoryComponent,
    HistoryItemComponent
  ],
  providers: [],
  exports: [
    HistoryComponent
  ]
})
export class HistoryModule {}
