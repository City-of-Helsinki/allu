import {Component, OnInit} from '@angular/core';
import {SafeResourceUrl, SafeUrl, DomSanitizer} from '@angular/platform-browser';
import {Observable} from 'rxjs/Observable';
import {Decision} from '../../../model/decision/Decision';
import {ApplicationState} from '../../../service/application/application-state';
import {DecisionHub} from '../../../service/decision/decision-hub';
import {ApplicationStatus} from '../../../model/application/application-status';

@Component({
  selector: 'decision-preview',
  template: require('./decision-preview.component.html'),
  styles: [
    require('./decision-preview.component.scss')
  ]
})
export class DecisionPreviewComponent implements OnInit {

  private pdfUrl: SafeResourceUrl;
  private pdfDownloadUrl: SafeUrl;
  private pdfLoaded: boolean;

  constructor(private sanitizer: DomSanitizer,
              private applicationState: ApplicationState,
              private decisionHub: DecisionHub) {}

  ngOnInit(): void {
    let appStatus = this.applicationState.application.statusEnum;
    this.decisionHub.fetch(this.applicationState.application.id)
        .subscribe(decision => this.providePdf(decision));
  }
  private providePdf(decision: Decision): void {
    let url = URL.createObjectURL(decision.pdf);
    this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    this.pdfDownloadUrl = this.sanitizer.bypassSecurityTrustUrl(url);
    this.pdfLoaded = true;
  }
}
