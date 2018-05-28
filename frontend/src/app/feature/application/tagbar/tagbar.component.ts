import {Component, Input, OnInit} from '@angular/core';
import {ApplicationTag} from '../../../model/application/tag/application-tag';
import {ApplicationTagType, manualTagTypes} from '../../../model/application/tag/application-tag-type';
import {Observable, of} from 'rxjs';
import {ApplicationStore} from '../../../service/application/application-store';
import {NotificationService} from '../../../service/notification/notification.service';
import {Application} from '../../../model/application/application';
import {MODIFY_ROLES, RoleType} from '../../../model/user/role-type';
import {Store} from '@ngrx/store';
import * as fromApplication from '../reducers';
import {Add, Remove} from '../actions/application-tag-actions';
import {map, startWith} from 'rxjs/internal/operators';

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
  availableTagTypes: Observable<string[]>;
  availableTagCount: Observable<number>;
  manualTagTypes: string[] = manualTagTypes.map(type => ApplicationTagType[type]);


  constructor(private applicationStore: ApplicationStore,
              private notification: NotificationService,
              private store: Store<fromApplication.State>) {}

  ngOnInit() {
    this.application = this.applicationStore.application;
    this.tags = this.store.select(fromApplication.getTags);
    this.availableTagTypes = this.tags.pipe(
      map(tags => tags.map(tag => tag.type)),
      startWith(this.manualTagTypes),
      map(current => this.manualTagTypes.filter(type => !current.some(c => type === c)))
    );
    this.availableTagCount = this.availableTagTypes.pipe(map(types => types.length));
  }

  canBeRemoved(typeName: string) {
    return this.manualTagTypes.indexOf(typeName) >= 0;
  }

  remove(tag: ApplicationTag): void {
    this.store.dispatch(new Remove(tag));
  }

  add(type: string): void {
    this.store.dispatch(new Add(new ApplicationTag(type, undefined, new Date())));
  }
}
