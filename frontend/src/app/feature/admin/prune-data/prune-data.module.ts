import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { RouterModule } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AlluCommonModule } from '@feature/common/allu-common.module';
import { EffectsModule } from '@ngrx/effects';
import { PruneDataEffects } from './store/prune-data.effects';
import { PruneDataComponent } from './prune-data.component';
import { pruneDataReducer } from './store/prune-data.reducer';
import { MatPaginatorModule } from '@angular/material/paginator';

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
    MatPaginatorModule
  ],
  exports: [
    PruneDataComponent
  ]
})
export class PruneDataModule { }
