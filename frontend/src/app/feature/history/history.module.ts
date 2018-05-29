import {NgModule} from '@angular/core';
import {EffectsModule} from '@ngrx/effects';
import {HistoryEffects} from './effects/history-effects';

@NgModule({
  imports: [
    EffectsModule.forFeature([HistoryEffects])
  ],
  declarations: [],
  providers: [],
  exports: []
})
export class HistoryModule {}
