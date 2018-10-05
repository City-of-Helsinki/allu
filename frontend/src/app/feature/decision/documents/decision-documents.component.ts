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
    switch (application.type) {
      case ApplicationType.PLACEMENT_CONTRACT: {
        return [DecisionTab.CONTRACT, DecisionTab.DECISION];
      }

      case ApplicationType.EXCAVATION_ANNOUNCEMENT: {
        const excavation = <ExcavationAnnouncement>application.extension;
        if (excavation.winterTimeOperation) {
          return [DecisionTab.DECISION, DecisionTab.OPERATIONAL_CONDITION, DecisionTab.WORK_FINISHED];
        } else {
          return [DecisionTab.DECISION, DecisionTab.WORK_FINISHED];
        }
      }

      default: {
        return [DecisionTab.DECISION];
      }
    }
  }
}
