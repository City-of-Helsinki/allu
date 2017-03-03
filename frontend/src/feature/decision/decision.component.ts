import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {DomSanitizer, SafeResourceUrl, SafeUrl} from '@angular/platform-browser';

import {ApplicationHub} from '../../service/application/application-hub';
import {Application} from '../../model/application/application';
import {DecisionHub} from '../../service/decision/decision-hub';
import {Decision} from '../../model/decision/Decision';
import {ProgressStep} from '../application/progressbar/progress-step';
import {NotificationService} from '../../service/notification/notification.service';

@Component({
  selector: 'decision',
  template: require('./decision.component.html'),
  styles: [require('./decision.component.scss')]
})
export class DecisionComponent implements OnInit {
  application: Application;
  private progressStep: number;
  private id: number;
  private pdfUrl: SafeResourceUrl;
  private pdfDownloadUrl: SafeUrl;
  private pdfLoaded: boolean;

  constructor(
    private sanitizer: DomSanitizer,
    private applicationHub: ApplicationHub,
    private decisionHub: DecisionHub,
    private route: ActivatedRoute) {
    this.progressStep = ProgressStep.DECISION;
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = Number(params['id']);

      this.applicationHub.getApplication(this.id).subscribe(
        application => this.application = application,
        err => NotificationService.error(err));

      this.decisionHub.generate(this.id).subscribe(decision => this.providePdf(decision));
    });
  }

  private providePdf(decision: Decision): void {
    let url = URL.createObjectURL(decision.pdf);
    this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    this.pdfDownloadUrl = this.sanitizer.bypassSecurityTrustUrl(url);
    this.pdfLoaded = true;
  }
}
