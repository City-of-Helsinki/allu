import {Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {Observable} from 'rxjs';

import {Application} from '../../model/application/application';
import {MapStore} from '../../service/map/map-store';


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

  constructor(private mapStore: MapStore) {
  }

  ngOnInit() {
    this.applications = this.mapStore.applications;
  }

  jobClick(application: Application) {
    this.mapStore.selectedApplicationChange(application);
  }
}
