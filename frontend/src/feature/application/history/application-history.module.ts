import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';

import {AlluCommonModule} from '../../common/allu-common.module';
import {ApplicationHistoryComponent} from './application-history.component';
import {HistoryHub} from '../../../service/history/history-hub';
import {HistoryService} from '../../../service/history/history-service';
import {ApplicationHistoryDetailsComponent} from './application-history-details.component';
import {ApplicationHistoryFormatter} from '../../../service/history/application-history-formatter';

@NgModule({
  imports: [
    FormsModule,
    AlluCommonModule,
    RouterModule.forChild([])
  ],
  declarations: [
    ApplicationHistoryComponent,
    ApplicationHistoryDetailsComponent
  ],
  providers: [
    HistoryHub,
    HistoryService,
    ApplicationHistoryFormatter
  ],
  entryComponents: [
    ApplicationHistoryDetailsComponent
  ]
})
export class ApplicationHistoryModule {}
