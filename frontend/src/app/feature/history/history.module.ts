import {NgModule} from '@angular/core';
import {EffectsModule} from '@ngrx/effects';
import {HistoryEffects} from './effects/history-effects';
import {HistoryComponent} from './history.component';
import {AlluCommonModule} from '../common/allu-common.module';
import {HistoryItemComponent} from './history-item.component';
import {HistoryItemGroupComponent} from './history-item-group.component';
import {RouterModule} from '@angular/router';
import {HistoryFormatter} from '../../service/history/history-formatter';
import {HistoryItemDescriptionComponent} from './history-item-description.component';
import {HistoryFieldComponent} from './field/history-field.component';
import {HistoryFieldsComponent} from './field/history-fields.component';
import {HistoryPreviewComponent} from './preview/history-preview.component';
import {HistoryItemPreviewComponent} from './preview/history-item-preview.component';
import {HistoryService} from '../../service/history/history-service';
import {HistoryFieldFormatter} from '../../service/history/history-field-formatter';
import {HistoryItemStatusComponent} from '@feature/history/history-item-status.component';

@NgModule({
  imports: [
    AlluCommonModule,
    EffectsModule.forFeature([HistoryEffects]),
    RouterModule.forChild([])
  ],
  declarations: [
    HistoryComponent,
    HistoryItemGroupComponent,
    HistoryItemComponent,
    HistoryItemDescriptionComponent,
    HistoryFieldsComponent,
    HistoryFieldComponent,
    HistoryPreviewComponent,
    HistoryItemPreviewComponent,
    HistoryItemStatusComponent
  ],
  providers: [
    HistoryFormatter,
    HistoryService,
    HistoryFieldFormatter
  ],
  exports: [
    HistoryComponent,
    HistoryPreviewComponent
  ]
})
export class HistoryModule {}
