import {NgModule} from '@angular/core';
import {ReactiveFormsModule, FormsModule} from '@angular/forms';
import {MatToolbarModule, MatInputModule} from '@angular/material';
import {AlluCommonModule} from '../common/allu-common.module';
import {SearchbarComponent} from './searchbar.component';
import {SelectionGroupModule} from '../common/selection-group/selection-group.module';

@NgModule({
  imports: [
    ReactiveFormsModule,
    FormsModule,
    AlluCommonModule,
    MatToolbarModule,
    MatInputModule,
    SelectionGroupModule
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
