import {Component, Input} from '@angular/core';
import {itemPaths, SidebarItem, SidebarItemType} from './sidebar-item';
import {ApplicationStore} from '../../service/application/application-store';

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

  constructor(private applicationStore: ApplicationStore) {}

  tabChange(type: SidebarItemType): void {
    this.applicationStore.changeTab(type);
  }
}
