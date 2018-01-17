import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationStatus, searchable} from '../../../model/application/application-status';
import {ApplicationType} from '../../../model/application/type/application-type';
import {User} from '../../../model/user/user';
import {CurrentUser} from '../../../service/user/current-user';
import {ApplicationTagType} from '../../../model/application/tag/application-tag-type';
import {CityDistrict} from '../../../model/common/city-district';
import {MapHub} from '../../../service/map/map-hub';
import {WorkQueueTab} from '../workqueue-tab';
import {WorkQueueHub} from '../workqueue-search/workqueue-hub';


const COMMON_MULTISELECT_VALUE = ['common'];

@Component({
  selector: 'workqueue-filter',
  templateUrl: './workqueue-filter.component.html',
  styleUrls: [
    './workqueue-filter.component.scss'
  ]
})
export class WorkQueueFilterComponent implements OnInit {
  queryForm: FormGroup;
  @Input() owners: Array<User>;
  districts: Observable<Array<CityDistrict>>;
  applicationStatuses = searchable.map(status => ApplicationStatus[status]);
  applicationTypes = EnumUtil.enumValues(ApplicationType);
  tagTypes = EnumUtil.enumValues(ApplicationTagType);
  tab: string;

  private typeCtrl: FormControl;
  private ownerCtrl: FormControl;
  private statusCtrl: FormControl;
  private tagsCtrl: FormControl;


  constructor(fb: FormBuilder,
              private mapHub: MapHub,
              private workQueueHub: WorkQueueHub,
              private currentUser: CurrentUser) {
    this.typeCtrl = fb.control(undefined);
    this.ownerCtrl = fb.control(undefined);
    this.statusCtrl = fb.control(undefined);
    this.tagsCtrl = fb.control([]);
    this.queryForm = fb.group({
      type: this.typeCtrl,
      owner: this.ownerCtrl,
      status: this.statusCtrl,
      districts: undefined,
      startTime: undefined,
      endTime: undefined,
      tags: this.tagsCtrl
    });
  }

  ngOnInit(): void {
    this.queryForm.valueChanges
      .distinctUntilChanged()
      .subscribe(query => this.workQueueHub.addSearchQuery(ApplicationSearchQuery.from(query)));

    this.districts = this.mapHub.districts();
    this.selectedTab = WorkQueueTab.OWN;
  }

  @Input() set selectedTab(tab: WorkQueueTab) {
    this.tab = WorkQueueTab[tab];
    this.queryForm.enable();
    this.typeCtrl.reset();
    this.statusCtrl.reset();

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
    const tags = this.tagsCtrl.value.filter(tag => tag !== ApplicationTagType[ApplicationTagType.WAITING]);
    this.tagsCtrl.setValue(tags);
    this.currentUser.user.subscribe(user => this.ownerCtrl.patchValue([user.userName]));
  }

  private waitingTabSelected(): void {
    this.tagsCtrl.patchValue([ApplicationTagType[ApplicationTagType.WAITING]]);
    this.ownerCtrl.patchValue([]);
  }

  private commonTabSelected(): void {
    this.tagTypes = EnumUtil.enumValues(ApplicationTagType);
    this.queryForm.patchValue({tags: [], type: COMMON_MULTISELECT_VALUE, status: COMMON_MULTISELECT_VALUE});
    this.ownerCtrl.patchValue([]);
    this.typeCtrl.disable();
    this.statusCtrl.disable();
  }
}
