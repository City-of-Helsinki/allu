import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MdToolbarModule} from '@angular2-material/toolbar';
import {MdInputModule} from '@angular2-material/input';
import {AlluCommonModule} from '../common/allu-common.module';
import {SearchbarComponent} from './searchbar.component';

@NgModule({
  imports: [
    FormsModule,
    AlluCommonModule,
    MdToolbarModule,
    MdInputModule
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
