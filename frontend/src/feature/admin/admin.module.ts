import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MdCardModule, MdExpansionModule, MdSlideToggleModule} from '@angular/material';

import {AlluCommonModule} from '../common/allu-common.module';
import {adminRoutes} from './admin.routing';
import {UserListComponent} from './user/user-list.component';
import {UserComponent} from './user/user.component';
import {AdminComponent} from './admin.component';
import {AdminNavComponent} from './nav/admin-nav.component';
import {DefaultAttachmentsComponent} from './default-attachment/default-attachments.component';
import {DefaultAttachmentComponent} from './default-attachment/default-attachment.component';
import {SelectionGroupModule} from '../common/selection-group/selection-group.module';
import {DefaultRecipientsComponent} from './default-recipients/default-recipients.component';
import {RecipientsByTypeComponent} from './default-recipients/recipients-by-type.component';
import {DefaultRecipientService} from '../../service/recipients/default-recipient.service';
import {DefaultRecipientHub} from '../../service/recipients/default-recipient-hub';

@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule.forChild(adminRoutes),
    FormsModule,
    ReactiveFormsModule,
    MdCardModule,
    MdExpansionModule,
    MdSlideToggleModule,
    SelectionGroupModule
  ],
  declarations: [
    AdminComponent,
    AdminNavComponent,
    UserListComponent,
    UserComponent,
    DefaultAttachmentsComponent,
    DefaultAttachmentComponent,
    DefaultRecipientsComponent,
    RecipientsByTypeComponent
  ],
  providers: [
    DefaultRecipientService,
    DefaultRecipientHub
  ]
})
export class AdminModule {}
