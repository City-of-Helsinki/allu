import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {DomSanitizer, SafeResourceUrl, SafeUrl} from '@angular/platform-browser';

import {ProgressStep, ProgressMode} from '../../feature/progressbar/progressbar.component';
import {ApplicationHub} from '../../service/application/application-hub';
import {Application} from '../../model/application/application';
import {DecisionHub} from '../../service/decision/decision-hub';
import {Decision} from '../../model/decision/Decision';

@Component({
  selector: 'decision',
  template: require('./decision.component.html'),
  styles: [require('./decision.component.scss')]
})
export class DecisionComponent implements OnInit {
  private progressStep: number;
  private progressMode: number;
  private id: number;
  private application: Application;
  private pdfUrl: SafeResourceUrl;
  private pdfDownloadUrl: SafeUrl;
  private pdfLoaded: boolean;

  constructor(
    private sanitizer: DomSanitizer,
    private applicationHub: ApplicationHub,
    private decisionHub: DecisionHub,
    private route: ActivatedRoute) {
    this.progressStep = ProgressStep.DECISION;
    this.progressMode = ProgressMode.EDIT;
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = Number(params['id']);
    });

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
    let url = URL.createObjectURL(decision.pdf);
    this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    this.pdfDownloadUrl = this.sanitizer.bypassSecurityTrustUrl(url);
    this.pdfLoaded = true;
  }
}
