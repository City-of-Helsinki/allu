import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MdToolbarModule} from '@angular2-material/toolbar';
import {AlluCommonModule} from '../common/allu-common.module';
import {SearchbarComponent} from './searchbar.component';

@NgModule({
  imports: [
    FormsModule,
    AlluCommonModule,
    MdToolbarModule
  ],
  declarations: [
    SearchbarComponent
  ],
  exports: [
    SearchbarComponent
  ],
  providers: []
})
export class SearchBarModule {}
