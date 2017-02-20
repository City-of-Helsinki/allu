import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';

import {AlluCommonModule} from '../../common/allu-common.module';
import {ApplicationHistoryComponent} from './application-history.component';
import {HistoryHub} from '../../../service/history/history-hub';
import {HistoryService} from '../../../service/history/history-service';

@NgModule({
  imports: [
    FormsModule,
    AlluCommonModule
  ],
  declarations: [
    ApplicationHistoryComponent
  ],
  providers: [
    HistoryHub,
    HistoryService
  ]
})
export class ApplicationHistoryModule {}
