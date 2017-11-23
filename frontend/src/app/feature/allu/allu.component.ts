import {Component, ViewEncapsulation} from '@angular/core';
import {Router, NavigationEnd, Event} from '@angular/router';

@Component({
  selector: 'allu',
  templateUrl: './allu.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: []
})
export class AlluComponent {

  constructor(private router: Router) {
    this.router.events.subscribe(event => this.handleRouteEvent(event));
  }

  private handleRouteEvent(event: Event) {
    if (event instanceof NavigationEnd) {
      // Scroll to top of the page when route changes
      document.body.scrollTop = 0;
    }
  }
}
