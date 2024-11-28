import { Component } from '@angular/core';
import { Application } from '@app/model/application/application';
import { ApplicationSearchDatasource } from '@app/service/application/application-search-datasource';
import {TimeUtil} from '../../../util/time.util';

import * as fromRoot from '@feature/allu/reducers';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';

@Component({
  selector: 'prune-applications',
  templateUrl: './prune-applications.component.html',
  styleUrls: ['./prune-applications.component.scss']
})
export class PruneApplicationsComponent {
  title = 'Moi'
  dataSource: ApplicationSearchDatasource;
  someSelected$: Observable<boolean>;
  allSelected$: Observable<boolean>;

  mockData = [
    {
      "id": 21,
      "applicationId": "KP2400006",
      "startTime": 1730844000000,
      "endTime": 1732831199999,
      "changeType": "STATUS_CHANGED",
      "changeTime": "2024-11-22T16:20:55.922223Z"
    },
    {
      "id": 20,
      "applicationId": "KP2400005",
      "startTime": 1730844000000,
      "endTime": 1732831199999,
      "changeType": "STATUS_CHANGED",
      "changeTime": TimeUtil.dateFromBackend("2024-11-22T16:19:38.05418Z")
    }
  ];

  displayedColumns = [
    'selected', 'applicationId', 'startTime', 'endTime', 'changeTime', 'changeType'
  ];

  constructor(private store: Store<fromRoot.State>) {
  }


  ngOnInit(): void {
    this.dataSource = new ApplicationSearchDatasource(this.store, null, null);
  }

  trackById(index: number, item: Application) {
    return item.id;
  }

  checkAll(): void {
    // this.store.dispatch(new ToggleSelectAll(ActionTargetType.Application));
  }

  selected(id: number): void {
    // return this.store.pipe(
    //   select(fromApplication.getSelectedApplications),
    //   map(selected => selected.indexOf(id) >= 0)
    // );
  }

  checkSingle(id: number) {
    // this.store.dispatch(new ToggleSelect(ActionTargetType.Application, id));
  }

  addToBasket(): void {
    // this.store.pipe(
    //   select(fromApplication.getSelectedApplications),
    //   take(1)
    // ).subscribe(selected => {
    //   this.store.dispatch(new AddMultiple(selected));
    //   this.store.dispatch(new ClearSelected(ActionTargetType.Application));
    // });
  }

}