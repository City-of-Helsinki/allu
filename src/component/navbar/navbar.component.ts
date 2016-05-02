import {Component} from 'angular2/core';
import {ROUTER_DIRECTIVES} from 'angular2/router';

@Component({
  selector: 'navbar',
  moduleId: module.id,
  templateUrl: './component/navbar/navbar.component.html',
  styles: [
    require('./navbar.component.scss')
  ],
  directives: [ROUTER_DIRECTIVES]
})
export class NavbarComponent {}
