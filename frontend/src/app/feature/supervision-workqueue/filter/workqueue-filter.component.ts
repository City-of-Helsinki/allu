import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {SupervisionTaskSearchCriteria} from '../../../model/application/supervision/supervision-task-search-criteria';
import {SupervisionTaskType} from '../../../model/application/supervision/supervision-task-type';
import {TimeUtil} from '../../../util/time.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {SupervisionWorkItemStore} from '../supervision-work-item-store';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationStatus} from '../../../model/application/application-status';
import {WorkQueueTab} from '../../workqueue/workqueue-tab';
import {CurrentUser} from '../../../service/user/current-user';
import {Subscription} from 'rxjs/Subscription';
import {CityDistrict} from '../../../model/common/city-district';
import {Observable} from 'rxjs/Observable';
import {CityDistrictService} from '../../../service/map/city-district.service';

interface SupervisionTaskSearchCriteriaForm {
  taskTypes: Array<string>;
  applicationId: string;
  after: string;
  before: string;
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
  }

  ngOnDestroy(): void {
    this.formSubscription.unsubscribe();
  }

  search(form: SupervisionTaskSearchCriteriaForm): void {
    const criteria = new SupervisionTaskSearchCriteria();
    criteria.taskTypes = form.taskTypes.map(type => SupervisionTaskType[type]);
    criteria.applicationId = form.applicationId;
    criteria.after = TimeUtil.getDateFromUi(form.after);
    criteria.before = TimeUtil.getDateFromUi(form.before);
    criteria.applicationTypes = form.applicationTypes.map(type => ApplicationType[type]);
    criteria.applicationStatus = form.applicationStatus.map(s => ApplicationStatus[s]);
    criteria.ownerId = form.ownerId;
    criteria.cityDistrictIds = form.cityDistrictIds;
    this.store.searchChange(criteria);
  }
}
