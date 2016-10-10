import {Component, OnInit, OnDestroy} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../model/application/application';
import {ApplicationHub} from '../../service/application/application-hub';

@Component({
  selector: 'workqueue',
  template: require('./workqueue.component.html'),
  styles: [
    require('./workqueue.component.scss')
  ]
})
export class WorkQueueComponent implements OnInit, OnDestroy {
  private applicationsQueue: Observable<Array<Application>>;
  private items: Array<string> = ['Ensimmäinen', 'Toinen', 'Kolmas', 'Neljäs', 'Viides'];
  private applicantName: string;

  constructor(private applicationHub: ApplicationHub) {
  }

  ngOnInit() {
    this.applicationsQueue = this.applicationHub.getApplications();
  }

  ngOnDestroy() {
  }

  public selected(value: any): void {
    console.log('Selected value is: ', value);
  }

  jobClick(job: Application) {
    // this.eventService.send(this, new ApplicationSelectionEvent(job));
  }
}
