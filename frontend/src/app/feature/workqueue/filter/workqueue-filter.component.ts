import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationStatus, workqueue_searchable} from '../../../model/application/application-status';
import {ApplicationType} from '../../../model/application/type/application-type';
import {User} from '../../../model/user/user';
import {ApplicationTagType} from '../../../model/application/tag/application-tag-type';
import {CityDistrict} from '../../../model/common/city-district';
import {WorkQueueTab} from '../workqueue-tab';
import {ApplicationWorkItemStore} from '../application-work-item-store';
import {Subject} from 'rxjs/Subject';
import {CityDistrictService} from '../../../service/map/city-district.service';

@Component({
  selector: 'workqueue-filter',
  templateUrl: './workqueue-filter.component.html',
  styleUrls: [
    './workqueue-filter.component.scss'
  ]
})
export class WorkQueueFilterComponent implements OnInit, OnDestroy {

  @Input() owners: Array<User>;

  queryForm: FormGroup;
  districts: Observable<Array<CityDistrict>>;
  applicationStatuses = workqueue_searchable.map(status => ApplicationStatus[status]);
  applicationTypes = EnumUtil.enumValues(ApplicationType);
  tagTypes = EnumUtil.enumValues(ApplicationTagType);
  tab: Observable<string>;

  private destroy = new Subject<boolean>();

  constructor(fb: FormBuilder,
              private cityDistrictService: CityDistrictService,
              private store: ApplicationWorkItemStore) {
    this.queryForm = fb.group({
      type: undefined,
      owner: undefined,
      status: undefined,
      districts: undefined,
      startTime: undefined,
      endTime: undefined,
      tags: []
    });
  }

  ngOnInit(): void {
    this.queryForm.valueChanges
      .takeUntil(this.destroy)
      .distinctUntilChanged()
      .debounceTime(300)
      .subscribe(query => this.store.searchChange(ApplicationSearchQuery.from(query)));

    this.districts = this.cityDistrictService.get();

    this.tab = this.store.changes
      .map(state => state.tab)
      .map(tab => WorkQueueTab[tab]);

    this.queryForm.patchValue(this.store.snapshot.search, {emitEvent: false});
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }
}
