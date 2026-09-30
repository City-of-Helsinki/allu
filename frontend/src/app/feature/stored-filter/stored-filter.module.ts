import {NgModule} from '@angular/core';
import {AlluCommonModule} from '../common/allu-common.module';
import {StoredFilterComponent} from './stored-filter.component';
import {StoredFilterService} from '../../service/stored-filter/stored-filter.service';
import {MatMenuModule} from '@angular/material/menu';
import {StoredFilterModalComponent} from './stored-filter-modal.component';
import {ReactiveFormsModule} from '@angular/forms';
import {StoredFilterStore} from '../../service/stored-filter/stored-filter-store';

@NgModule({
    imports: [
        AlluCommonModule,
        MatMenuModule,
        ReactiveFormsModule
    ],
    declarations: [
        StoredFilterComponent,
        StoredFilterModalComponent
    ],
    exports: [
        StoredFilterComponent
    ],
    providers: [
        StoredFilterService,
        StoredFilterStore
    ]
})
export class StoredFilterModule {}
