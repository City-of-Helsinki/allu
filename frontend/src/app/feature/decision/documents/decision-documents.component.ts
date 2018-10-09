import {Component, OnInit} from '@angular/core';
import {DecisionTab} from '@feature/decision/documents/decision-tab';
import {Observable} from 'rxjs/index';
import {Store} from '@ngrx/store';
import * as fromApplication from '@feature/application/reducers';
import {map} from 'rxjs/internal/operators';
import {ApplicationType} from '@model/application/type/application-type';
import {Application} from '@model/application/application';
import {ExcavationAnnouncement} from '@model/application/excavation-announcement/excavation-announcement';


@Component({
  selector: 'decision-documents',
  templateUrl: './decision-documents.component.html',
  styleUrls: ['./decision-documents.component.scss']
})
export class DecisionDocumentsComponent implements OnInit {
  tabs$: Observable<DecisionTab[]>;

  constructor(private store: Store<fromApplication.State>) {}

  ngOnInit(): void {
    this.tabs$ = this.store.select(fromApplication.getCurrentApplication).pipe(
      map(app => this.getAvailableTabs(app)),
    );
  }

  private getAvailableTabs(application: Application): DecisionTab[] {
    let tabs = [DecisionTab.DECISION];
    switch (application.type) {
      case ApplicationType.PLACEMENT_CONTRACT: {
        tabs = [DecisionTab.CONTRACT].concat(tabs);
        break;
      }

      case ApplicationType.EXCAVATION_ANNOUNCEMENT: {
        const excavation = <ExcavationAnnouncement>application.extension;
        if (excavation.winterTimeOperation) {
          tabs = tabs.concat(DecisionTab.OPERATIONAL_CONDITION);
        }

        if (excavation.workFinished) {
          tabs = tabs.concat(DecisionTab.WORK_FINISHED);
        }
        break;
      }
    }
    return tabs;
  }
}
