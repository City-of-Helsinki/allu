import {Component, Input, Output, EventEmitter} from '@angular/core';
import {SidebarItemType, SidebarItem} from './sidebar-item';
import {ApplicationState} from '../../service/application/application-state';

@Component({
  selector: 'sidebar',
  template: require('./sidebar.component.html'),
  styles: [
    require('./sidebar.component.scss')
  ]
})
export class SidebarComponent {
  @Input() items: Array<SidebarItem> = [{type: 'BASIC_INFO'}];

  constructor(private applicationState: ApplicationState) {}

  tabChange(type: SidebarItemType): void {
    this.applicationState.notifyTabChange(type);
  }
}
