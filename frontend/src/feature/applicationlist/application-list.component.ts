import {Component, OnInit, OnDestroy} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../model/application/application';
import {SearchbarFilter} from '../../service/searchbar-filter';
import {MapHub} from '../../service/map-hub';


@Component({
  selector: 'application-list',
  template: require('./application-list.component.html'),
  styles: [
    require('./application-list.component.scss')
  ]
})

export class ApplicationListComponent implements OnInit, OnDestroy {

  private applications: Observable<Array<Application>>;

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
