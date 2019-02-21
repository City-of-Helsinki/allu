import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Observable} from 'rxjs';

import {Application} from '@model/application/application';
import {MapStore} from '@service/map/map-store';
import {Add} from '@feature/project/actions/application-basket-actions';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';


@Component({
  selector: 'application-list',
  templateUrl: './application-list.component.html',
  styleUrls: [
    './application-list.component.scss'
  ],
  encapsulation: ViewEncapsulation.None
})
export class ApplicationListComponent implements OnInit {

  applications: Observable<Array<Application>>;

  constructor(private mapStore: MapStore,
              private store: Store<fromRoot.State>) {}

  ngOnInit() {
    this.applications = this.mapStore.applications;
  }

  focusOnApplication(application: Application) {
    this.mapStore.selectedApplicationChange(application);
  }

  addToBasket(applicationId: number): void {
    this.store.dispatch(new Add(applicationId));
  }
}
