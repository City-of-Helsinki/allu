import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MdToolbarModule, MdInputModule} from '@angular/material';
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
