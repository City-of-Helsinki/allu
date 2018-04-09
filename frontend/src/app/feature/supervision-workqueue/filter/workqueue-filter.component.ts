import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {SupervisionTaskSearchCriteria} from '../../../model/application/supervision/supervision-task-search-criteria';
import {SupervisionTaskType} from '../../../model/application/supervision/supervision-task-type';
import {TimeUtil} from '../../../util/time.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {SupervisionWorkItemStore} from '../supervision-work-item-store';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationStatus} from '../../../model/application/application-status';
import {CurrentUser} from '../../../service/user/current-user';
import {Subscription} from 'rxjs/Subscription';
import {CityDistrict} from '../../../model/common/city-district';
import {Observable} from 'rxjs/Observable';
import {CityDistrictService} from '../../../service/map/city-district.service';
import {ArrayUtil} from '../../../util/array-util';

interface SupervisionTaskSearchCriteriaForm {
  taskTypes: Array<string>;
  applicationId: string;
  after: Date;
  before: Date;
  applicationTypes: Array<string>;
  applicationStatus: Array<string>;
  ownerId: number;
  cityDistrictIds: Array<number>;
}

@Component({
  selector: 'supervision-workqueue-filter',
  templateUrl: './workqueue-filter.component.html',
  styleUrls: [
    './workqueue-filter.component.scss'
  ]
})
export class WorkQueueFilterComponent implements OnInit, OnDestroy {
  queryForm: FormGroup;
  taskTypes = EnumUtil.enumValues(SupervisionTaskType);
  applicationTypes = EnumUtil.enumValues(ApplicationType);
  applicationStatusTypes = EnumUtil.enumValues(ApplicationStatus);
  districts: Observable<Array<CityDistrict>>;

  private formSubscription: Subscription;

  constructor(
    private fb: FormBuilder,
    private store: SupervisionWorkItemStore,
    private currentUser: CurrentUser,
    private cityDistrictService: CityDistrictService) {
    this.queryForm = this.fb.group({
      taskTypes: [[]],
      applicationId: [undefined],
      after: [undefined],
      before: [undefined],
      applicationTypes: [[]],
      applicationStatus: [[]],
      ownerId: [undefined],
      cityDistrictIds: [[]]
    });
  }

  ngOnInit(): void {
    this.formSubscription = this.queryForm.valueChanges
      .distinctUntilChanged()
      .debounceTime(300)
      .subscribe(values => this.search(values));

    this.districts = this.cityDistrictService.get();

    this.queryForm.patchValue(this.formValues(this.store.snapshot.search), {emitEvent: false});
  }

  ngOnDestroy(): void {
    this.formSubscription.unsubscribe();
  }

  search(form: SupervisionTaskSearchCriteriaForm): void {
    const criteria = new SupervisionTaskSearchCriteria();
    criteria.taskTypes = ArrayUtil.map(form.taskTypes, type => SupervisionTaskType[type]);
    criteria.applicationId = form.applicationId;
    criteria.after = form.after;
    criteria.before = form.before;
    criteria.applicationTypes = ArrayUtil.map(form.applicationTypes, type => ApplicationType[type]);
    criteria.applicationStatus = ArrayUtil.map(form.applicationStatus, s => ApplicationStatus[s]);
    criteria.ownerId = form.ownerId;
    criteria.cityDistrictIds = form.cityDistrictIds;
    this.store.searchChange(criteria);
  }

  private formValues(criteria: SupervisionTaskSearchCriteria): SupervisionTaskSearchCriteriaForm {
    return {
      taskTypes: ArrayUtil.map(criteria.taskTypes, type => SupervisionTaskType[type]),
      applicationId: criteria.applicationId,
      after: criteria.after,
      before: criteria.before,
      applicationTypes: ArrayUtil.map(criteria.applicationTypes, type => ApplicationType[type]),
      applicationStatus: ArrayUtil.map(criteria.applicationStatus, s => ApplicationStatus[s]),
      ownerId: criteria.ownerId,
      cityDistrictIds: criteria.cityDistrictIds
    };
  }
}
