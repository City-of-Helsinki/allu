import {NgModule, ModuleWithProviders} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {MdCardModule} from '@angular/material';

import {LocationComponent} from './location.component';
import {AlluCommonModule} from '../../common/allu-common.module';
import {SearchBarModule} from '../../searchbar/searchbar.module';
import {MapModule} from '../../map/map.module';
import {ProgressBarModule} from '../../progressbar/progressbar.module';
import {LocationState} from '../../../service/application/location-state';
import {TypeModule} from '../type/type.module';

@NgModule({
  imports: [
    FormsModule,
    RouterModule,
    AlluCommonModule,
    MdCardModule,
    SearchBarModule,
    MapModule,
    ProgressBarModule,
    TypeModule
  ],
  declarations: [
    LocationComponent
  ],
  providers: [
    LocationState
  ]
})
export class LocationModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: LocationModule,
      providers: [
        LocationState
      ]
    };
  }
}
