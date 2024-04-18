import {NgModule} from '@angular/core';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {OwnerNotificationModalComponent} from '@feature/application/owner-notification/owner-notification-modal.component';
import {HistoryModule} from '@feature/history/history.module';

@NgModule({
    imports: [
        AlluCommonModule,
        HistoryModule
    ],
    declarations: [
        OwnerNotificationModalComponent
    ],
    providers: [],
    exports: [
        OwnerNotificationModalComponent
    ]
})
export class OwnerNotificationModule {}
