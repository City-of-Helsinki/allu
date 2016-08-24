import {Component, Input, OnInit} from '@angular/core';

import {MdToolbar} from '@angular2-material/toolbar';

@Component({
  selector: 'searchbar',
  moduleId: module.id,
  template: require('./searchbar.component.html'),
  styles: [
    require('./searchbar.component.scss')
  ],
  directives: [MdToolbar]
})

export class SearchbarComponent {}
