import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MapComponent} from './map.component';
import {MapUtil} from '../../service/map/map.util';
import {MapService} from '../../service/map/map.service';
import {MapLayerService} from '../../service/map/map-layer.service';
import {FixedLocationService} from '../../service/map/fixed-location.service';
import {CityDistrictService} from '../../service/map/city-district.service';
import {MapDataService} from '../../service/map/map-data-service';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    MapComponent
  ],
  exports: [
    MapComponent
  ],
  providers: [
    MapUtil,
    MapService,
    MapLayerService,
    MapDataService,
    FixedLocationService,
    CityDistrictService
  ]
})
export class MapModule {}
