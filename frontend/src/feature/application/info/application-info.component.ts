import {Component, OnInit} from '@angular/core';
import {Application} from '../../../model/application/application';
import {ApplicationState} from '../../../service/application/application-state';

@Component({
  selector: 'application-info',
  viewProviders: [],
  template: require('./application-info.component.html'),
  styles: []
})
export class ApplicationInfoComponent implements OnInit {

  application: Application;

  constructor(private applicationState: ApplicationState) {}

  ngOnInit(): void {
    this.application = this.applicationState.application;
  }
}
