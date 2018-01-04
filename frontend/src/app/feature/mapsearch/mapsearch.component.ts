import {Component, ViewEncapsulation} from '@angular/core';
import {Router} from '@angular/router';
import {ApplicationStore} from '../../service/application/application-store';

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

  constructor(private router: Router, private applicationStore: ApplicationStore) {
  }

  showAdvancedSearch() {
    this.sidenavOpen = true;
  }

  hideAdvancedSearch() {
    this.sidenavOpen = false;
  }

  newApplication() {
    this.applicationStore.reset();
    this.router.navigate(['/applications/location']);
  }
}
