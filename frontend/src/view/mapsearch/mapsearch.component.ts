import {Component} from '@angular/core';
import {MdToolbar} from '@angular2-material/toolbar';

import {ToolbarComponent} from '../../component/toolbar/toolbar.component';
import {SearchbarComponent} from '../../component/searchbar/searchbar.component';

import {MapComponent} from '../../component/map/map.component';
import {ApplicationListComponent} from '../../component/application/list/application-list.component';

@Component({
  selector: 'mapsearch',
  viewProviders: [],
  moduleId: module.id,
  template: require('./mapsearch.component.html'),
  styles: [
    require('./mapsearch.component.scss')
  ],
  directives: [
    ToolbarComponent,
    MdToolbar,
    MapComponent,
    SearchbarComponent,
    ApplicationListComponent
  ]
})

export class MapSearchComponent {}
