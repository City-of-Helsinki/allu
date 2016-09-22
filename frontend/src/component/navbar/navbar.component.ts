import {Component} from '@angular/core';
import {ROUTER_DIRECTIVES} from '@angular/router';

@Component({
  selector: 'navbar',
  moduleId: module.id,
  template: require('./navbar.component.html'),
  styles: [
    require('./navbar.component.scss')
  ],
  directives: [ROUTER_DIRECTIVES]
})
export class NavbarComponent {}
