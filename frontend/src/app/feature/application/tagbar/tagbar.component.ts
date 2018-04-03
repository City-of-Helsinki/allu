import {Component, Input, OnInit} from '@angular/core';
import {ApplicationTag} from '../../../model/application/tag/application-tag';
import {ApplicationTagType, manualTagTypes} from '../../../model/application/tag/application-tag-type';
import {Observable} from 'rxjs/Observable';
import {ApplicationStore} from '../../../service/application/application-store';
import {NotificationService} from '../../../service/notification/notification.service';
import {Application} from '../../../model/application/application';
import {MODIFY_ROLES, RoleType} from '../../../model/user/role-type';

@Component({
  selector: 'tagbar',
  templateUrl: './tagbar.component.html',
  styleUrls: [
    './tagbar.component.scss'
  ]
})
export class TagBarComponent implements OnInit {
  @Input() readonly: boolean;

  MODIFY_ROLES = MODIFY_ROLES.map(role => RoleType[role]);

  application: Observable<Application>;
  tags: Observable<Array<ApplicationTag>>;
  manualTagTypes = manualTagTypes.map(type => ApplicationTagType[type]);

  constructor(private applicationStore: ApplicationStore) {}

  ngOnInit() {
    this.application = this.applicationStore.application;
    this.tags = this.applicationStore.tags;
  }

  canBeRemoved(typeName: string) {
    return this.manualTagTypes.indexOf(typeName) >= 0;
  }

  remove(index: number): void {
    const tags = this.applicationStore.snapshot.tags;
    tags.splice(index, 1);
    this.saveTags(tags);
  }

  add(type: string): void {
    const tags = this.applicationStore.snapshot.tags;
    if (!tags.some(tag => tag.type === type)) {
      this.saveTags(tags.concat(new ApplicationTag(type, undefined, new Date())));
    }
  }

  private saveTags(tags: Array<ApplicationTag>): void {
    if (this.readonly) {
      // on readonly mode we should save tags immediately
      this.applicationStore.saveTags(tags)
        .subscribe(
          app => {}, // Nothing to do with updated app
          error => NotificationService.error(error)
        );
    } else {
      this.applicationStore.changeTags(tags);
    }
  }
}
