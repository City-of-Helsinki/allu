import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {SearchComponent} from './search.component';
import {AlluCommonModule} from '../common/allu-common.module';
import {MatLegacyPaginatorModule as MatPaginatorModule} from '@angular/material/legacy-paginator';
import {MatSortModule} from '@angular/material/sort';
import {MatLegacyTableModule as MatTableModule} from '@angular/material/legacy-table';


@NgModule({
  imports: [
    ReactiveFormsModule,
    FormsModule,
    AlluCommonModule,
    RouterModule.forChild([]),
    MatTableModule,
    MatSortModule,
    MatPaginatorModule
  ],
  declarations: [
    SearchComponent
  ],
  providers: []
})
export class SearchModule {}
