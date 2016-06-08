import {Component} from '@angular/core';
import {ToolbarComponent} from '../../component/toolbar/toolbar.component';

import {MapComponent} from '../../component/map/map.component';
import {LocationSearchComponent} from '../../component/locationsearch/locationsearch.component';

@Component({
  selector: 'mapsearch',
  viewProviders: [],
  moduleId: module.id,
  template: require('./mapsearch.component.html'),
  styles: [
    require('./mapsearch.component.scss')
  ],
  directives: [ToolbarComponent, MapComponent, LocationSearchComponent]
})

export class MapSearchComponent {}
