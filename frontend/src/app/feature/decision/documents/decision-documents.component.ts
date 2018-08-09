import {Component, Input, OnInit} from '@angular/core';
import {DecisionTab} from '@feature/decision/documents/decision-tab';
import {Observable} from 'rxjs/index';
import {Store} from '@ngrx/store';
import * as fromApplication from '@feature/application/reducers';
import {map} from 'rxjs/internal/operators';
import {ApplicationType} from '@model/application/type/application-type';



@Component({
  selector: 'decision-documents',
  templateUrl: './decision-documents.component.html',
  styleUrls: ['./decision-documents.component.scss']
})
export class DecisionDocumentsComponent implements OnInit {
  tabs$: Observable<string[]>;

  constructor(private store: Store<fromApplication.State>) {}

  ngOnInit(): void {
    this.tabs$ = this.store.select(fromApplication.getCurrentApplication).pipe(
      map(application => application.typeEnum),
      map(type => this.getAvailableTabs(type)),
    );
  }

  private getAvailableTabs(applicationType: ApplicationType): string[] {
    let tabs = [];
    if (applicationType === ApplicationType.PLACEMENT_CONTRACT) {
      tabs = [DecisionTab.CONTRACT, DecisionTab.DECISION];
    } else {
      tabs = [DecisionTab.DECISION];
    }
    return tabs.map(t => DecisionTab[t]);
  }
}
