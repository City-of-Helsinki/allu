import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MapComponent} from './map.component';
import {MapUtil} from '../../service/map/map.util';
import {MapService} from '../../service/map/map.service';
import {MapLayerService} from '../../service/map/map-layer.service';

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
    MapLayerService
  ]
})
export class MapModule {}
