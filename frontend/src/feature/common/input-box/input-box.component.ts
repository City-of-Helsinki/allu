import {Component, Input, ViewEncapsulation} from '@angular/core';

@Component({
  selector: 'input-box',
  template: require('./input-box.component.html'),
  styles: [
    require('./input-box.component.scss')
  ],
  encapsulation: ViewEncapsulation.None
})
export class InputBoxComponent {
  @Input() placeholder: string;
}
