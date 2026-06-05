import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import * as filesaver from 'file-saver';
import {AttachmentHub} from './attachment-hub';
import {DefaultAttachmentInfo} from '@model/application/attachment/default-attachment-info';
import {ApplicationType} from '@model/application/type/application-type';
import {AttachmentType} from '@model/application/attachment/attachment-type';
import {ArrayUtil} from '@util/array-util';
import {CurrentUser} from '@service/user/current-user';
import {map} from 'rxjs/internal/operators';
import {combineLatest} from 'rxjs';
import {Observable} from 'rxjs/internal/Observable';
import {BehaviorSubject} from 'rxjs/internal/BehaviorSubject';

@Component({
  selector: 'default-attachments',
  templateUrl: './default-attachments.component.html',
  styleUrls: ['./default-attachments.component.scss']
})
export class DefaultAttachmentsComponent implements OnInit {
  @Input() applicationType: ApplicationType;
  @Input() attachmentType: AttachmentType;
  @Input() isAllowedToEdit = true;
  @Output() add = new EventEmitter<DefaultAttachmentInfo>();
  @Output() remove = new EventEmitter<DefaultAttachmentInfo>();

  defaultAttachments$: Observable<DefaultAttachmentInfo[]>;

  private _selectedAttachments$: BehaviorSubject<DefaultAttachmentInfo[]> = new BehaviorSubject<DefaultAttachmentInfo[]>([]);

  constructor(private attachmentHub: AttachmentHub, private currentUser: CurrentUser) {}

  ngOnInit(): void {
    this.defaultAttachments$ = combineLatest([
      this.attachmentHub.defaultAttachmentInfosBy(this.applicationType, this.attachmentType),
      this._selectedAttachments$
    ]).pipe(
      map(([existing, selected]) => existing.concat(selected)),
      map(attachments => attachments.filter(ArrayUtil.uniqueItem(a => a.id))),
      map(das => das.sort(ArrayUtil.naturalSort((item: DefaultAttachmentInfo) => item.name)))
    );

    this.currentUser.hasRole(['ROLE_CREATE_APPLICATION', 'ROLE_PROCESS_APPLICATION'])
      .subscribe(hasValidRole => this.isAllowedToEdit = this.isAllowedToEdit && hasValidRole);
  }

  isSelected(attachment: DefaultAttachmentInfo): boolean {
    return this.selectedAttachments.some(
      selected => this.isSameAttachment(selected, attachment)
    );
  }

  onToggle(attachment: DefaultAttachmentInfo, checked: boolean): void {
    const da = attachment;
    da.file = new Blob(['empty']);
    if (checked) {
      this.add.emit(da);
    } else {
      this.remove.emit(da);
    }
  }

  private isSameAttachment(a: DefaultAttachmentInfo, b: DefaultAttachmentInfo): boolean {
    if (!a || !b) {
      return false;
    }
    if (a.id != null && b.id != null) {
      return a.id === b.id;
    }
    return a.name === b.name && a.type === b.type;
  }

  download(attachment: DefaultAttachmentInfo) {
    this.attachmentHub.download(attachment.id)
      .subscribe(file => filesaver.saveAs(file, attachment.name));
  }

  @Input()
  set selectedAttachments(attachments: DefaultAttachmentInfo[]) {
    this._selectedAttachments$.next(attachments);
  }

  get selectedAttachments() {
    return this._selectedAttachments$.getValue();
  }
}
