import {Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../model/application/application';
import {MapHub} from '../../service/map/map-hub';


@Component({
  selector: 'application-list',
  templateUrl: './application-list.component.html',
  styleUrls: [
    './application-list.component.scss'
  ],
  encapsulation: ViewEncapsulation.None
})

export class ApplicationListComponent implements OnInit, OnDestroy {

  applications: Observable<Array<Application>>;

  constructor(private mapHub: MapHub) {
  }

  ngOnInit() {
    this.applications = this.mapHub.applications();
  }

  ngOnDestroy() {
  }

  jobClick(application: Application) {
    this.mapHub.selectApplication(application);
  }
}
