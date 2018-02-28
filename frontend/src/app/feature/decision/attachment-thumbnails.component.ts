import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {AttachmentInfo} from '../../model/application/attachment/attachment-info';
import {AttachmentService} from '../../service/attachment-service';
import {FileUtil} from '../../util/file-util';
import * as filesaver from 'file-saver';

interface AttachmentThumbnail {
  id: number;
  name: string;
  icon: string;
}

@Component({
  selector: 'attachment-thumbnails',
  templateUrl: './attachment-thumbnails.component.html',
  styleUrls: ['./attachment-thumbnails.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AttachmentThumbnailsComponent implements OnChanges {
  @Input() attachments: AttachmentInfo[];

  thumbnails: AttachmentThumbnail[];

  constructor(private attachmentService: AttachmentService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.thumbnails = this.attachments
      .map(a => this.toThumbnail(a));
  }

  download(thumbnail: AttachmentThumbnail): void {
    this.attachmentService.download(thumbnail.id)
      .subscribe(file => filesaver.saveAs(file, thumbnail.name));
  }

  private toThumbnail(attachment: AttachmentInfo): AttachmentThumbnail {
    return {
      id: attachment.id,
      name: attachment.description ? attachment.description : attachment.name,
      icon: FileUtil.iconForMimeType(attachment.mimeType)
    };
  }
}
