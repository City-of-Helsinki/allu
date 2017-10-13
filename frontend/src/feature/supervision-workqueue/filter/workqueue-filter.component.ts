import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import '../../../rxjs-extensions.ts';
import {SupervisionTaskSearchCriteria} from '../../../model/application/supervision/supervision-task-search-criteria';
import {SupervisionTaskType} from '../../../model/application/supervision/supervision-task-type';
import {TimeUtil} from '../../../util/time.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {SupervisionWorkItemStore} from '../supervision-work-item-store';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationStatus} from '../../../model/application/application-status';
import {WorkQueueTab} from '../../workqueue/workqueue-tab';
import {CurrentUser} from '../../../service/user/current-user';

interface SupervisionTaskSearchCriteriaForm {
  taskTypes: Array<string>;
  applicationId: string;
  after: string;
  before: string;
  applicationTypes: Array<string>;
  applicationStatus: Array<string>;
  handlerId: number;
}

@Component({
  selector: 'supervision-workqueue-filter',
  template: require('./workqueue-filter.component.html'),
  styles: [
    require('./workqueue-filter.component.scss')
  ]
})
export class WorkQueueFilterComponent implements OnInit {
  queryForm: FormGroup;
  taskTypes = EnumUtil.enumValues(SupervisionTaskType);
  applicationTypes = EnumUtil.enumValues(ApplicationType);
  applicationStatusTypes = EnumUtil.enumValues(ApplicationStatus);

  constructor(
    private fb: FormBuilder,
    private store: SupervisionWorkItemStore,
    private currentUser: CurrentUser) {
    this.queryForm = this.fb.group({
      taskTypes: [[]],
      applicationId: [undefined],
      after: [undefined],
      before: [undefined],
      applicationTypes: [[]],
      applicationStatus: [[]],
      handlerId: [undefined]
    });
  }

  ngOnInit(): void {
    this.queryForm.valueChanges
      .debounceTime(300)
      .subscribe(values => this.search(values));

    this.store.changes
      .map(change => change.tab)
      .distinctUntilChanged()
      .subscribe(tab => this.onTabChange(tab));
  }

  search(form: SupervisionTaskSearchCriteriaForm): void {
    let criteria = new SupervisionTaskSearchCriteria();
    criteria.taskTypes = form.taskTypes.map(type => SupervisionTaskType[type]);
    criteria.applicationId = form.applicationId;
    criteria.after = TimeUtil.getDateFromUi(form.after);
    criteria.before = TimeUtil.getDateFromUi(form.before);
    criteria.applicationTypes = form.applicationTypes.map(type => ApplicationType[type]);
    criteria.applicationStatus = form.applicationStatus.map(s => ApplicationStatus[s]);
    criteria.handlerId = form.handlerId;
    this.store.update({search: criteria});
  }

  private onTabChange(tab: WorkQueueTab): void {
    if (WorkQueueTab.OWN === tab) {
      this.currentUser.user.subscribe(user => this.queryForm.patchValue({handlerId: user.id}));
    } else {
      this.queryForm.patchValue({handlerId: undefined});
    }

  }
}
