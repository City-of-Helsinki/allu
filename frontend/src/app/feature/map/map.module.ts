import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MapComponent} from './map.component';
import {MapUtil} from '../../service/map/map.util';
import {MapService} from '../../service/map/map.service';
import {MapLayerService} from '../../service/map/map-layer.service';
import {FixedLocationService} from '../../service/map/fixed-location.service';
import {CityDistrictService} from '../../service/map/city-district.service';
import {MapDataService} from '../../service/map/map-data-service';
import {MapPopupService} from '../../service/map/map-popup.service';
import {MapPopupComponent} from './map-popup.component';
import {RouterModule} from '@angular/router';
import {AlluCommonModule} from '../common/allu-common.module';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    AlluCommonModule
  ],
  declarations: [
    MapComponent,
    MapPopupComponent
  ],
  exports: [
    MapComponent
  ],
  providers: [
    MapUtil,
    MapService,
    MapLayerService,
    MapPopupService,
    MapDataService,
    FixedLocationService,
    CityDistrictService
  ],
  entryComponents: [
    MapPopupComponent
  ]
})
export class MapModule {}
