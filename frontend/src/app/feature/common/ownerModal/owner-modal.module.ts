import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {OwnerModalComponent} from './owner-modal.component';
import {CommonModule} from '@angular/common';
import {AlluCommonModule} from '../allu-common.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        AlluCommonModule
    ],
    declarations: [
        OwnerModalComponent
    ],
    exports: [
        OwnerModalComponent
    ]
})
export class OwnerModalModule {}
