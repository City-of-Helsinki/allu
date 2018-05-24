import {Component, OnInit} from '@angular/core';
import {EMPTY, Observable} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';

import {AttachmentHub} from '../../application/attachment/attachment-hub';
import {DefaultAttachmentInfo} from '../../../model/application/attachment/default-attachment-info';
import {translateArray} from '../../../util/translations';
import {ContentRow} from '../../../model/common/content-row';
import {MaterializeUtil} from '../../../util/materialize.util';
import {FixedLocationService} from '../../../service/map/fixed-location.service';
import {Some} from '../../../util/option';
import {map} from 'rxjs/internal/operators';

@Component({
  selector: 'default-attachments',
  templateUrl: './default-attachments.component.html',
  styleUrls: []
})
export class DefaultAttachmentsComponent implements OnInit {
  translateArray = translateArray;
  attachmentRows: Array<ContentRow<DefaultAttachmentInfo>>;
  attachmentType: string;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private attachmentHub: AttachmentHub,
              private fixedLocationService: FixedLocationService) {
  }

  ngOnInit(): void {
    this.loadAttachmentInfos();
    this.route.data.subscribe((data: {attachmentType: string}) => this.attachmentType = data.attachmentType);
  }

  remove(row: ContentRow<DefaultAttachmentInfo>): void {
    this.attachmentHub.removeDefaultAttachment(row.id)
      .subscribe(
        result => {
          MaterializeUtil.toast('Liite ' + row.content.name + ' poistettu');
          this.loadAttachmentInfos();
        },
        error => MaterializeUtil.toast('Liitteen ' + row.content.name + ' poistaminen epäonnistui'));
  }

  goToDetails(col: number, row: ContentRow<DefaultAttachmentInfo>): void {
    // undefined and 0 should not trigger navigation
    if (col) {
      this.router.navigate([row.id], {relativeTo: this.route});
    }
  }

  areaName(areaId: number): Observable<string> {
    return Some(areaId)
      .map(id => this.fixedLocationService.areaById(id).pipe(map(area => area.name)))
      .orElse(EMPTY);
  }

  private loadAttachmentInfos(): void {
    this.attachmentHub.defaultAttachmentInfos().pipe(
      map(attachmentInfos => attachmentInfos
        .filter(ai => ai.type === this.attachmentType)
        .map(ai => new ContentRow(ai)))
    ).subscribe(rows => this.attachmentRows = rows);
  }
}
