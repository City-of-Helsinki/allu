import {ModuleWithProviders, NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {MatLegacyCardModule as MatCardModule} from '@angular/material/legacy-card';

import {LocationComponent} from './location.component';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {SearchBarModule} from '@feature/searchbar/searchbar.module';
import {MapModule} from '@feature/map/map.module';
import {ProgressBarModule} from '@feature/application/progressbar/progressbar.module';
import {TypeModule} from '@feature/application/type/type.module';
import {StoredLocationsComponent} from './stored-locations.component';
import {StoreModule} from '@ngrx/store';
import {reducersProvider, reducersToken} from '@feature/application/location/reducers';
import {GeometrySelectComponent} from '@feature/application/location/geometry-select/geometry-select.component';
import {EffectsModule} from '@ngrx/effects';
import {UserAreaEffects} from '@feature/application/location/effects/user-area-effects.service';
import {UserAreasComponent} from '@feature/application/location/user-areas/user-areas.component';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    StoreModule.forFeature('location', reducersToken),
    EffectsModule.forFeature([UserAreaEffects]),
    AlluCommonModule,
    MatCardModule,
    SearchBarModule,
    MapModule,
    ProgressBarModule,
    TypeModule
  ],
  declarations: [
    LocationComponent,
    StoredLocationsComponent,
    GeometrySelectComponent,
    UserAreasComponent,
  ],
  providers: [
    reducersProvider
  ],
  exports: [
    StoredLocationsComponent
  ]
})
export class LocationModule {
  static forRoot(): ModuleWithProviders<LocationModule> {
    return {
      ngModule: LocationModule
    };
  }
}
