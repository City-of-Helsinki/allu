import {Component, Input} from '@angular/core';
import {itemPaths, SidebarItem, SidebarItemType} from './sidebar-item';
import {ApplicationState} from '../../service/application/application-state';

@Component({
  selector: 'sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: [
    './sidebar.component.scss'
  ]
})
export class SidebarComponent {
  @Input() items: Array<SidebarItem> = [{type: 'BASIC_INFO'}];
  itemPaths = itemPaths;

  constructor(private applicationState: ApplicationState) {}

  tabChange(type: SidebarItemType): void {
    this.applicationState.notifyTabChange(type);
  }
}
