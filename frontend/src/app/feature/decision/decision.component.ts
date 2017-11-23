import {Component, OnInit} from '@angular/core';
import {DomSanitizer, SafeResourceUrl, SafeUrl} from '@angular/platform-browser';
import {Application} from '../../model/application/application';
import {DecisionHub} from '../../service/decision/decision-hub';
import {Decision} from '../../model/decision/Decision';
import {stepFrom} from '../application/progressbar/progress-step';
import {ApplicationStatus} from '../../model/application/application-status';
import {ApplicationState} from '../../service/application/application-state';
import {StatusChangeInfo} from '../../model/application/status-change-info';

@Component({
  selector: 'decision',
  templateUrl: './decision.component.html',
  styleUrls: ['./decision.component.scss']
})
export class DecisionComponent implements OnInit {
  application: Application;
  progressStep: number;
  pdfUrl: SafeResourceUrl;
  pdfDownloadUrl: SafeUrl;
  pdfLoaded: boolean;

  constructor(
    private sanitizer: DomSanitizer,
    private applicationState: ApplicationState,
    private decisionHub: DecisionHub) {}

  ngOnInit(): void {
    this.application = this.applicationState.application;
    this.progressStep = stepFrom(ApplicationStatus[this.application.status]);
    this.decisionHub.fetch(this.application.id)
      .subscribe(decision => this.providePdf(decision));
  }

  onDecisionConfirm(changeInfo: StatusChangeInfo): void {
    this.decisionHub.fetch(this.application.id)
      .subscribe(decision => this.providePdf(decision));
  }

  private providePdf(decision: Decision): void {
    let url = URL.createObjectURL(decision.pdf);
    this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    this.pdfDownloadUrl = this.sanitizer.bypassSecurityTrustUrl(url);
    this.pdfLoaded = true;
  }
}
