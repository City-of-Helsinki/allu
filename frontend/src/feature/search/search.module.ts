import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SearchComponent} from './search.component';
import {AlluCommonModule} from '../common/allu-common.module';

@NgModule({
  imports: [
    ReactiveFormsModule,
    FormsModule,
    AlluCommonModule
  ],
  declarations: [
    SearchComponent
  ],
  providers: []
})
export class SearchModule {}
