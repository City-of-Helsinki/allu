import {Component, ViewEncapsulation} from '@angular/core';

@Component({
  selector: 'mapsearch',
  viewProviders: [],
  templateUrl: './mapsearch.component.html',
  styleUrls: [
    './mapsearch.component.scss'
  ],
  encapsulation: ViewEncapsulation.None
})

export class MapSearchComponent {
  sidenavOpen = false;

  showAdvancedSearch() {
    this.sidenavOpen = true;
  }

  hideAdvancedSearch() {
    this.sidenavOpen = false;
  }
}
