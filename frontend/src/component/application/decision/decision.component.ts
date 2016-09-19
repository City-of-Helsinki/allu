import {Component, OnInit} from '@angular/core';
import {RouteParams} from '@angular/router-deprecated';

import {ProgressbarComponent, ProgressStep, ProgressMode} from '../../progressbar/progressbar.component';
import {ApplicationBasicInfoComponent} from '../decision/application.basic-info.component';
import {DecisionActionsComponent} from '../decision/decision-actions.component';
import {ApplicationHub} from '../../../service/application/application-hub';
import {Application} from '../../../model/application/application';
import {DecisionHub} from '../../../service/decision/decision-hub';
import {Decision} from '../../../model/decision/Decision';

@Component({
  selector: 'decision',
  moduleId: module.id,
  template: require('./decision.component.html'),
  styles: [require('./decision.component.scss')],
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
  private pdfUrl: string;
  private pdfLoaded: boolean;

  constructor(private applicationHub: ApplicationHub, private decisionHub: DecisionHub, private params: RouteParams) {
    this.progressStep = ProgressStep.DECISION;
    this.progressMode = ProgressMode.EDIT;

    this.id = Number(params.get('id'));
  }

  ngOnInit(): void {
    this.applicationHub.applications().subscribe(applications => this.handleApplications(applications));
    this.applicationHub.addApplicationSearch(this.id);

    this.decisionHub.decisions().subscribe(decisions => this.handleDecisions(decisions));
    this.decisionHub.generate(this.id);
  }

  private handleApplications(applications: Array<Application>): void {
    this.application = applications.find(app => app.id === this.id);
  }

  private handleDecisions(decisions: Array<Decision>): void {
    let decision = decisions.find(d => d.applicationId === this.id);
    this.pdfUrl = URL.createObjectURL(decision.pdf);
    this.pdfLoaded = true;
  }
}
