import {Component} from 'angular2/core';

import {NavbarComponent} from '../navbar/navbar.component';

@Component({
  selector: 'toolbar',
  moduleId: module.id,
  templateUrl: './component/toolbar/toolbar.component.html',
  styles: [
    require('./toolbar.component.scss')
  ],
  directives: [NavbarComponent]
})
export class ToolbarComponent {}
