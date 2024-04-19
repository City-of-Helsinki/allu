import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatLegacyRadioModule as MatRadioModule} from '@angular/material/legacy-radio';
import {AlluCommonModule} from '../../common/allu-common.module';
import {DistributionComponent} from './distribution.component';
import {DistributionListComponent} from './distribution-list/distribution-list.component';
import {DistributionSelectionComponent} from '@feature/application/distribution/distribution-list/distribution-selection.component';
import {DistributionModalComponent} from '@feature/application/distribution/distribution-modal.component';

@NgModule({
    imports: [
        FormsModule,
        ReactiveFormsModule,
        AlluCommonModule,
        MatRadioModule
    ],
    declarations: [
        DistributionComponent,
        DistributionListComponent,
        DistributionSelectionComponent,
        DistributionModalComponent,
    ],
    exports: [
        DistributionComponent,
        DistributionListComponent,
        DistributionSelectionComponent,
        DistributionModalComponent
    ]
})
export class DistributionModule {}
