import {Component} from '@angular/core';

import {NavbarComponent} from '../navbar/navbar.component';

@Component({
  selector: 'toolbar',
  template: require('./toolbar.component.html'),
  styles: [
    require('./toolbar.component.scss')
  ],
  directives: [NavbarComponent]
})
export class ToolbarComponent {}
