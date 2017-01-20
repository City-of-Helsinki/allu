import {Component, Input} from '@angular/core';
import {SidebarItemType, SidebarItem} from './sidebar-item';

@Component({
  selector: 'sidebar',
  template: require('./sidebar.component.html'),
  styles: [
    require('./sidebar.component.scss')
  ]
})
export class SidebarComponent {
  @Input() items: Array<SidebarItem> = [{type: 'BASIC_INFO'}];
}
