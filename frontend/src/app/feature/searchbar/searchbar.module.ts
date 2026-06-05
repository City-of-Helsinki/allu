import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatLegacyInputModule as MatInputModule} from '@angular/material/legacy-input';
import {MatToolbarModule} from '@angular/material/toolbar';
import {AlluCommonModule} from '../common/allu-common.module';
import {SearchbarComponent} from './searchbar.component';
import {MatChipsModule} from '@angular/material/chips';
import {StoredFilterModule} from '../stored-filter/stored-filter.module';
import {MapModule} from '@feature/map/map.module';

@NgModule({
  imports: [
    ReactiveFormsModule,
    FormsModule,
    AlluCommonModule,
    MatToolbarModule,
    MatInputModule,
    MatChipsModule,
    StoredFilterModule,
    MapModule
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
