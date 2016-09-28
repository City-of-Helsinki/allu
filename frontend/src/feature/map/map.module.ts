import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MapComponent} from './map.component';
import {MapUtil} from '../../service/map.util';

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
    MapUtil
  ]
})
export class MapModule {}
