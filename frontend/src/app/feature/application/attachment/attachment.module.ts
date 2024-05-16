import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AlluCommonModule} from '../../common/allu-common.module';
import {AttachmentHub} from './attachment-hub';
import {AttachmentService} from '../../../service/attachment-service';
import {AttachmentComponent} from './attachment.component';
import {AttachmentsComponent} from './attachments.component';
import {DefaultAttachmentsComponent} from './default-attachments.component';
import {SelectionGroupModule} from '../../common/selection-group/selection-group.module';
import {MatLegacySlideToggleModule as MatSlideToggleModule} from '@angular/material/legacy-slide-toggle';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    SelectionGroupModule,
    MatSlideToggleModule
  ],
  declarations: [
    AttachmentsComponent,
    AttachmentComponent,
    DefaultAttachmentsComponent
  ],
  providers: [
    AttachmentHub,
    AttachmentService
  ]
})
export class AttachmentModule {}
