import {Component, Input, OnInit} from '@angular/core';

import {MdToolbar} from '@angular2-material/toolbar';

@Component({
  selector: 'progressbar',
  moduleId: module.id,
  template: require('./progressbar.component.html'),
  styles: [
    require('./progressbar.component.scss')
  ],
  directives: [MdToolbar]
})
export class ProgressbarComponent implements OnInit {
  @Input() step: number;
  @Input('type') type: string;
  private width: number;

  constructor() {
    this.type = 'NOT SET';
  }

  ngOnInit() {
    switch (this.step) {
      case 1:
        this.width = 9;
        break;
      case 2:
        this.width = 25;
        break;
      case 3:
        this.width = 42;
        break;
      default:
        this.width = 9;
        break;
    }
  }
}
