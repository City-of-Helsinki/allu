import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {SearchComponent} from './search.component';
import {AlluCommonModule} from '../common/allu-common.module';
import {SearchService} from '../../service/search.service';

@NgModule({
  imports: [
    FormsModule,
    AlluCommonModule
  ],
  declarations: [
    SearchComponent
  ],
  providers: [
    SearchService
  ]
})
export class SearchModule {}
