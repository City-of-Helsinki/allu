import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Observable} from 'rxjs';

import {Application} from '@model/application/application';
import {MapStore} from '@service/map/map-store';
import {Add} from '@feature/project/actions/application-basket-actions';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromMap from '@feature/map/reducers';
import {BehaviorSubject} from 'rxjs/internal/BehaviorSubject';
import {map} from 'rxjs/operators';
import {combineLatest} from 'rxjs';

const SHOW_COUNT_STEP = 20;

@Component({
  selector: 'application-list',
  templateUrl: './application-list.component.html',
  styleUrls: [
    './application-list.component.scss'
  ],
  encapsulation: ViewEncapsulation.None
})
export class ApplicationListComponent implements OnInit {

  applications$: Observable<Application[]>;
  show: BehaviorSubject<number> = new BehaviorSubject<number>(SHOW_COUNT_STEP);
  remaining$: Observable<number>;

  constructor(private mapStore: MapStore,
              private store: Store<fromRoot.State>) {}

  ngOnInit() {
    this.applications$ = combineLatest([this.show, this.store.pipe(select(fromMap.getApplications))]).pipe(
      map(([show, applications]) => applications.length > show ? applications.slice(0, show) : applications)
    );

    this.remaining$ = combineLatest([this.show, this.store.pipe(select(fromMap.getApplications))]).pipe(
      map(([show, applications]) => applications.length - show)
    );
  }

  focusOnApplication(application: Application) {
    this.mapStore.selectedApplicationChange(application);
  }

  addToBasket(applicationId: number): void {
    this.store.dispatch(new Add(applicationId));
  }

  showMore(): void {
    this.show.next(this.show.getValue() + SHOW_COUNT_STEP);
  }
}
