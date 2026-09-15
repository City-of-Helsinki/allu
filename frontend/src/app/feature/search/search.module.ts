import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {SearchComponent} from './search.component';
import {AlluCommonModule} from '../common/allu-common.module';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table';
import {HyphenateDistrictPipe} from '@app/pipe/hyphenate-district.pipe';


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
    SearchComponent,
    HyphenateDistrictPipe
  ],
  providers: []
})
export class SearchModule {}
