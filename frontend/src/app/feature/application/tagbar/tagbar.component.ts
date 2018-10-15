import {Component, Input, OnInit} from '@angular/core';
import {ApplicationTag} from '@model/application/tag/application-tag';
import {ApplicationTagType, manuallyAddedTagTypes, removableTagTypes} from '@model/application/tag/application-tag-type';
import {Observable} from 'rxjs';
import {NotificationService} from '@feature/notification/notification.service';
import {MODIFY_ROLES, RoleType} from '@model/user/role-type';
import {select, Store} from '@ngrx/store';
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

  tags: Observable<Array<ApplicationTag>>;
  availableTagTypes: Observable<string[]>;
  availableTagCount: Observable<number>;
  manuallyAddedTags: string[] = manuallyAddedTagTypes.map(type => ApplicationTagType[type]);
  removableTags: string[] = removableTagTypes.map(type => ApplicationTagType[type]);


  constructor(private notification: NotificationService,
              private store: Store<fromApplication.State>) {}

  ngOnInit() {
    this.tags = this.store.pipe(select(fromApplication.getTags));
    this.availableTagTypes = this.tags.pipe(
      map(tags => tags.map(tag => tag.type)),
      startWith(this.manuallyAddedTags),
      map(current => this.manuallyAddedTags.filter(type => !current.some(c => type === c)))
    );
    this.availableTagCount = this.availableTagTypes.pipe(map(types => types.length));
  }

  canBeRemoved(typeName: string) {
    return this.removableTags.indexOf(typeName) >= 0;
  }

  remove(tag: ApplicationTag): void {
    this.store.dispatch(new Remove(tag));
  }

  add(type: string): void {
    this.store.dispatch(new Add(new ApplicationTag(type, undefined, new Date())));
  }
}
