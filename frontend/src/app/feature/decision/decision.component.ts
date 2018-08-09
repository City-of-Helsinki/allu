import {Component, OnInit} from '@angular/core';
import {Application} from '../../model/application/application';
import {ApplicationStore} from '../../service/application/application-store';
import {Observable} from 'rxjs';
import {AttachmentInfo} from '../../model/application/attachment/attachment-info';
import {map} from 'rxjs/internal/operators';

@Component({
  selector: 'decision',
  templateUrl: './decision.component.html',
  styleUrls: ['./decision.component.scss']
})
export class DecisionComponent implements OnInit {
  applicationChanges: Observable<Application>;
  decisionAttachments: Observable<Array<AttachmentInfo>>;


  constructor(
    private applicationStore: ApplicationStore) {}

  ngOnInit(): void {
    this.applicationChanges = this.applicationStore.application;
    this.decisionAttachments = this.applicationStore.attachments.pipe(
      map(attachments => attachments.filter(a => a.decisionAttachment))
    );
  }
}
