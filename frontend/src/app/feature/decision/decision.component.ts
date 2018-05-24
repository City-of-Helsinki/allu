import {Component, OnDestroy, OnInit} from '@angular/core';
import {DomSanitizer, SafeResourceUrl, SafeUrl} from '@angular/platform-browser';
import {Application} from '../../model/application/application';
import {DecisionHub} from '../../service/decision/decision-hub';
import {Decision} from '../../model/decision/Decision';
import {stepFrom} from '../application/progressbar/progress-step';
import {ApplicationStatus} from '../../model/application/application-status';
import {ApplicationStore} from '../../service/application/application-store';
import {Observable, Subject} from 'rxjs';
import {NumberUtil} from '../../util/number.util';
import {AttachmentInfo} from '../../model/application/attachment/attachment-info';
import {filter, map, takeUntil} from 'rxjs/internal/operators';

@Component({
  selector: 'decision',
  templateUrl: './decision.component.html',
  styleUrls: ['./decision.component.scss']
})
export class DecisionComponent implements OnInit, OnDestroy {
  applicationChanges: Observable<Application>;
  decisionAttachments: Observable<Array<AttachmentInfo>>;
  progressStep: number;
  pdfUrl: SafeResourceUrl;
  pdfDownloadUrl: SafeUrl;
  pdfLoaded: boolean;
  processing = false;

  private destroy = new Subject<boolean>();

  constructor(
    private sanitizer: DomSanitizer,
    private applicationStore: ApplicationStore,
    private decisionHub: DecisionHub) {}

  ngOnInit(): void {
    this.applicationChanges = this.applicationStore.application;
    this.decisionAttachments = this.applicationStore.attachments.pipe(
      map(attachments => attachments.filter(a => a.decisionAttachment))
    );

    this.applicationChanges.pipe(
      takeUntil(this.destroy),
      filter(app => NumberUtil.isDefined(app.id))
    ).subscribe(app => {
        this.progressStep = stepFrom(ApplicationStatus[app.status]);
        this.decisionHub.fetch(app.id)
          .subscribe(decision => this.providePdf(decision));
    });

    this.applicationStore.changes.pipe(
      map(change => change.processing),
      takeUntil(this.destroy)
    ).subscribe(processing => this.processing = processing);
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  onDecisionConfirm(): void {
    this.decisionHub.fetch(this.applicationStore.snapshot.application.id)
      .subscribe(decision => this.providePdf(decision));
  }

  private providePdf(decision: Decision): void {
    const url = URL.createObjectURL(decision.pdf);
    this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    this.pdfDownloadUrl = this.sanitizer.bypassSecurityTrustUrl(url);
    this.pdfLoaded = true;
  }
}
