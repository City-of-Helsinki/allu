import {Component} from '@angular/core';
import {Router, NavigationEnd, Event} from '@angular/router';
import {ViewEncapsulation} from '@angular/core';
import {MaterializeUtil} from '../../util/materialize.util';

@Component({
  selector: 'allu',
  template: require('./allu.component.html'),
  encapsulation: ViewEncapsulation.None,
  styles: [
    require('../../assets/main.scss'),
    require('../../assets/search.scss')
  ]
})
export class AlluComponent {

  constructor(private router: Router) {
    this.router.events.subscribe(event => this.handleRouteEvent(event));
  }

  private handleRouteEvent(event: Event) {
    if (event instanceof NavigationEnd) {
      // Scroll to top of the page when route changes
      document.body.scrollTop = 0;
      // To resize materialize textareas based on their content
      MaterializeUtil.resizeTextArea('.materialize-textarea', 50);
    }
  }
}
