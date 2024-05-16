import {Component, Input, OnInit} from '@angular/core';
import {ApplicationTag} from '@model/application/tag/application-tag';
import {ApplicationTagType} from '@model/application/tag/application-tag-type';
import {combineLatest, Observable} from 'rxjs';
import {NotificationService} from '@feature/notification/notification.service';
import {MODIFY_ROLES, RoleType} from '@model/user/role-type';
import {select, Store} from '@ngrx/store';
import * as fromApplication from '../reducers';
import * as fromAuth from '@feature/auth/reducers';
import {Add, Remove} from '../actions/application-tag-actions';
import {map} from 'rxjs/operators';
import {ArrayUtil} from '@util/array-util';

@Component({
  selector: 'tagbar',
  templateUrl: './tagbar.component.html',
  styleUrls: [
    './tagbar.component.scss'
  ]
})
export class TagBarComponent implements OnInit {
  @Input() readonly: boolean;

  MODIFY_ROLES = MODIFY_ROLES.concat(RoleType.ROLE_MANAGE_SURVEY);

  tags: Observable<ApplicationTag[]>;
  availableTagTypes: Observable<ApplicationTagType[]>;
  availableTagCount: Observable<number>;


  constructor(private notification: NotificationService,
              private store: Store<fromApplication.State>) {}

  ngOnInit() {
    this.tags = this.store.pipe(select(fromApplication.getTags));
    this.availableTagTypes = combineLatest([
      this.store.pipe(select(fromAuth.getAllowedTags)),
      this.tags
    ]).pipe(
      map(([allowed, current]) => allowed.filter(type => !current.some(c => type === c.type))),
      map(tags => tags.sort(ArrayUtil.naturalSortTranslated(['application.tag.type'], tag => tag)))
    );

    this.availableTagCount = this.availableTagTypes.pipe(map(types => types.length));
  }

  canBeRemoved(type: ApplicationTagType): Observable<boolean> {
    return this.store.pipe(
      select(fromAuth.getRemovableTags),
      map(removable => removable.indexOf(type) >= 0)
    );
  }

  remove(tag: ApplicationTag): void {
    this.store.dispatch(new Remove(tag.type));
  }

  add(type: ApplicationTagType): void {
    this.store.dispatch(new Add(new ApplicationTag(type, undefined, new Date())));
  }
}
