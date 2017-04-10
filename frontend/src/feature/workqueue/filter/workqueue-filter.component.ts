import {Component, OnInit, Input, Output} from '@angular/core';
import {FormGroup, FormBuilder} from '@angular/forms';
import '../../../rxjs-extensions.ts';

import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationStatus} from '../../../model/application/application-status';
import {ApplicationType} from '../../../model/application/type/application-type';
import {PICKADATE_PARAMETERS} from '../../../util/time.util';
import {User} from '../../../model/common/user';
import {CurrentUser} from '../../../service/user/current-user';
import {ApplicationTagType} from '../../../model/application/tag/application-tag-type';
import {Observable} from 'rxjs';
import {CityDistrict} from '../../../model/common/city-district';
import {MapHub} from '../../../service/map/map-hub';
import {WorkQueueTab} from '../workqueue-tab';
import {WorkQueueHub} from '../workqueue-search/workqueue-hub';

const HANDLER_FIELD = 'handler';
const TAGS_FIELD = 'tags';
const TYPE_FIELD = 'type';
const STATUS_FIELD = 'status';

@Component({
  selector: 'workqueue-filter',
  template: require('./workqueue-filter.component.html'),
  styles: [
    require('./workqueue-filter.component.scss')
  ]
})
export class WorkQueueFilterComponent implements OnInit {
  queryForm: FormGroup;
  @Input() handlers: Array<User>;
  pickadateParams = PICKADATE_PARAMETERS;
  districts: Observable<Array<CityDistrict>>;
  applicationStatuses = EnumUtil.enumValues(ApplicationStatus);
  applicationTypes = EnumUtil.enumValues(ApplicationType);
  tagTypes = EnumUtil.enumValues(ApplicationTagType);
  tab: string;

  constructor(fb: FormBuilder, private mapHub: MapHub, private workQueueHub: WorkQueueHub) {
    this.queryForm = fb.group({
      type: undefined,
      handler: undefined,
      status: undefined,
      districts: undefined,
      startTime: undefined,
      endTime: undefined,
      tags: [[]]
    });
  }

  ngOnInit(): void {
    this.queryForm.valueChanges
      .distinctUntilChanged()
      .subscribe(query => this.workQueueHub.addSearchQuery(ApplicationSearchQuery.from(query)));

    this.districts = this.mapHub.districts();
  }

  @Input() set selectedTab(tab: WorkQueueTab) {
    this.queryForm.enable();
    this.tab = WorkQueueTab[tab];
    if (WorkQueueTab.OWN === tab) {
      this.ownTabSelected();
    } else if (WorkQueueTab.WAITING === tab) {
      this.waitingTabSelected();
    } else {
      this.commonTabSelected();
    }
  }

  private ownTabSelected(): void {
    // initiate search with the set username filter
    this.tagTypes = EnumUtil.enumValues(ApplicationTagType).filter(tagType => tagType !== ApplicationTagType[ApplicationTagType.WAITING]);
    // remove waiting tag filter if such was selected
    let tagControl = this.queryForm.get(TAGS_FIELD);
    let tags = this.queryForm.value.tags.filter(tag => tag !== ApplicationTagType[ApplicationTagType.WAITING]);
    tagControl.setValue(tags);

    CurrentUser.userName().do(userName => this.setHandlers([userName]));
  }

  private waitingTabSelected(): void {
    let tags = this.queryForm.get(TAGS_FIELD);
    tags.setValue([ApplicationTagType[ApplicationTagType.WAITING]]);
    this.setHandlers([]);
    this.workQueueHub.addSearchQuery(ApplicationSearchQuery.from(this.queryForm.value));
  }

  private commonTabSelected(): void {
    this.tagTypes = EnumUtil.enumValues(ApplicationTagType);
    this.queryForm.patchValue({tags: []});
    this.setHandlers([]);
    this.queryForm.get(TYPE_FIELD).disable();
    this.queryForm.get(STATUS_FIELD).disable();
  }

  private setHandlers(handlers: Array<string>): void {
    let control = this.queryForm.get(HANDLER_FIELD);
    control.setValue(handlers);
    this.workQueueHub.addSearchQuery(ApplicationSearchQuery.from(this.queryForm.value));
  }
}
