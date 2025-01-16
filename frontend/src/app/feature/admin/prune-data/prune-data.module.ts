import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { MatLegacyTableModule as MatTableModule } from '@angular/material/legacy-table';
import { MatSortModule } from '@angular/material/sort';
import { RouterModule } from '@angular/router';
import { MatLegacyTabsModule as MatTabsModule } from '@angular/material/legacy-tabs';
import { MatLegacyCheckboxModule as MatCheckboxModule } from '@angular/material/legacy-checkbox';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AlluCommonModule } from '@feature/common/allu-common.module';
import { EffectsModule } from '@ngrx/effects';
import { PruneDataEffects } from './store/prune-data.effects';
import { PruneDataComponent } from './prune-data.component';
import { pruneDataReducer } from './store/prune-data.reducer';

@NgModule({
  declarations: [
    PruneDataComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatSortModule,
    MatTabsModule,
    MatCheckboxModule,
    FlexLayoutModule,
    AlluCommonModule,
    StoreModule.forFeature('pruneData', pruneDataReducer),
    EffectsModule.forFeature([PruneDataEffects]),
  ],
  exports: [
    PruneDataComponent
  ]
})
export class PruneDataModule { } 