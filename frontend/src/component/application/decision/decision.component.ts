import {Component, OnInit} from '@angular/core';
import {RouteParams} from '@angular/router-deprecated';

import {ProgressbarComponent, ProgressStep, ProgressMode} from '../../progressbar/progressbar.component';
import {ApplicationBasicInfoComponent} from '../decision/application.basic-info.component';
import {DecisionActionsComponent} from '../decision/decision-actions.component';
import {ApplicationHub} from '../../../service/application/application-hub';
import {Application} from '../../../model/application/application';

@Component({
  selector: 'decision',
  moduleId: module.id,
  template: require('./decision.component.html'),
  styles: [],
  directives: [
    ProgressbarComponent,
    ApplicationBasicInfoComponent,
    DecisionActionsComponent
  ]
})
export class DecisionComponent implements OnInit {
  private progressStep: number;
  private progressMode: number;
  private id: number;
  private application: Application;

  constructor(private applicationHub: ApplicationHub, private params: RouteParams) {
    this.progressStep = ProgressStep.DECISION;
    this.progressMode = ProgressMode.EDIT;

    this.id = Number(params.get('id'));
  }

  ngOnInit(): void {
    this.applicationHub.applications().subscribe(applications => this.handleApplications(applications));
    this.applicationHub.addApplicationSearch(this.id);
  }

  private handleApplications(applications: Array<Application>): void {
    this.application = applications.find(app => app.id === this.id);
  }
}
