import {Component, OnInit} from '@angular/core';
import {DomSanitizer, SafeResourceUrl, SafeUrl} from '@angular/platform-browser';
import {Decision} from '../../../model/decision/Decision';
import {ApplicationStore} from '../../../service/application/application-store';
import {DecisionHub} from '../../../service/decision/decision-hub';

@Component({
  selector: 'decision-preview',
  templateUrl: './decision-preview.component.html',
  styleUrls: [
    './decision-preview.component.scss'
  ]
})
export class DecisionPreviewComponent implements OnInit {

  pdfUrl: SafeResourceUrl;
  pdfDownloadUrl: SafeUrl;
  pdfLoaded: boolean;

  constructor(private sanitizer: DomSanitizer,
              private applicationStore: ApplicationStore,
              private decisionHub: DecisionHub) {}

  ngOnInit(): void {
    this.decisionHub.fetch(this.applicationStore.application.id)
        .subscribe(decision => this.providePdf(decision));
  }
  private providePdf(decision: Decision): void {
    const url = URL.createObjectURL(decision.pdf);
    this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    this.pdfDownloadUrl = this.sanitizer.bypassSecurityTrustUrl(url);
    this.pdfLoaded = true;
  }
}
