import {Component, OnInit} from '@angular/core';
import {DomSanitizer, SafeResourceUrl, SafeUrl} from '@angular/platform-browser';
import {Application} from '../../model/application/application';
import {DecisionHub} from '../../service/decision/decision-hub';
import {Decision} from '../../model/decision/Decision';
import {stepFrom} from '../application/progressbar/progress-step';
import {ApplicationStatus} from '../../model/application/application-status';
import {ApplicationState} from '../../service/application/application-state';

@Component({
  selector: 'decision',
  template: require('./decision.component.html'),
  styles: [require('./decision.component.scss')]
})
export class DecisionComponent implements OnInit {
  application: Application;
  private progressStep: number;
  private pdfUrl: SafeResourceUrl;
  private pdfDownloadUrl: SafeUrl;
  private pdfLoaded: boolean;

  constructor(
    private sanitizer: DomSanitizer,
    private applicationState: ApplicationState,
    private decisionHub: DecisionHub) {}

  ngOnInit(): void {
    this.application = this.applicationState.application;
    this.progressStep = stepFrom(ApplicationStatus[this.application.status]);
    this.decisionHub.generate(this.application.id).subscribe(decision => this.providePdf(decision));
  }

  private providePdf(decision: Decision): void {
    let url = URL.createObjectURL(decision.pdf);
    this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    this.pdfDownloadUrl = this.sanitizer.bypassSecurityTrustUrl(url);
    this.pdfLoaded = true;
  }
}
