import {Component, Input} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {SidebarItemType} from './sidebar-item-type';

@Component({
  selector: 'sidebar',
  template: require('./sidebar.component.html'),
  styles: [
    require('./sidebar.component.scss')
  ]
})
export class SidebarComponent {
  @Input() items: Array<SidebarItemType> = ['BASIC_INFO'];
}
