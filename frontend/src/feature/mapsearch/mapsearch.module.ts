import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MdToolbarModule} from '@angular2-material/toolbar';
import {MdCardModule} from '@angular2-material/card';
import {MapModule} from '../map/map.module';
import {MapSearchComponent} from './mapsearch.component';
import {SearchBarModule} from '../searchbar/searchbar.module';
import {ApplicationListComponent} from '../applicationlist/application-list.component';

@NgModule({
  imports: [
    CommonModule,
    MdToolbarModule,
    MdCardModule,
    MapModule,
    SearchBarModule
  ],
  declarations: [
    MapSearchComponent,
    ApplicationListComponent
  ]
})
export class MapSearchModule {}
