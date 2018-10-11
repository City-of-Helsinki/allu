import {Component, OnInit} from '@angular/core';
import {DecisionTab} from '@feature/decision/documents/decision-tab';
import {combineLatest, Observable} from 'rxjs/index';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import * as fromSupervision from '@feature/application/supervision/reducers';
import {map} from 'rxjs/internal/operators';
import {ApplicationType} from '@model/application/type/application-type';
import {Application} from '@model/application/application';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {SupervisionTaskStatusType} from '@model/application/supervision/supervision-task-status-type';


@Component({
  selector: 'decision-documents',
  templateUrl: './decision-documents.component.html',
  styleUrls: ['./decision-documents.component.scss']
})
export class DecisionDocumentsComponent implements OnInit {
  tabs$: Observable<DecisionTab[]>;

  constructor(private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    this.tabs$ = combineLatest(
      this.store.pipe(select(fromApplication.getCurrentApplication)),
      this.store.pipe(select(fromSupervision.hasTask(SupervisionTaskType.OPERATIONAL_CONDITION, SupervisionTaskStatusType.APPROVED))),
      this.store.pipe(select(fromSupervision.hasTask(SupervisionTaskType.FINAL_SUPERVISION, SupervisionTaskStatusType.APPROVED)))
    ).pipe(
      map(([application, operational, workFinished]) => this.getAvailableTabs(application, operational, workFinished))
    );
  }

  private getAvailableTabs(application: Application, operationalCondition, workFinished): DecisionTab[] {
    let tabs = [DecisionTab.DECISION];

    switch (application.type) {
      case ApplicationType.PLACEMENT_CONTRACT: {
        tabs = [DecisionTab.CONTRACT].concat(tabs);
        break;
      }

      case ApplicationType.EXCAVATION_ANNOUNCEMENT: {
        if (operationalCondition) {
          tabs = tabs.concat(DecisionTab.OPERATIONAL_CONDITION);
        }

        if (workFinished) {
          tabs = tabs.concat(DecisionTab.WORK_FINISHED);
        }
        break;
      }
    }
    return tabs;
  }
}
