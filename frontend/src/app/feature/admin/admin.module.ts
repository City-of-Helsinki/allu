import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatCardModule, MatExpansionModule, MatSlideToggleModule} from '@angular/material';

import {AlluCommonModule} from '../common/allu-common.module';
import {adminRoutes} from './admin.routing';
import {UserListComponent} from './user/user-list.component';
import {UserComponent} from './user/user.component';
import {AdminComponent} from './admin.component';
import {AdminNavComponent} from './nav/admin-nav.component';
import {DefaultAttachmentsComponent} from './default-attachment/default-attachments.component';
import {DefaultAttachmentComponent} from './default-attachment/default-attachment.component';
import {SelectionGroupModule} from '@feature/common/selection-group/selection-group.module';
import {DefaultRecipientsComponent} from './default-recipients/default-recipients.component';
import {RecipientsByTypeComponent} from './default-recipients/recipients-by-type.component';
import {DefaultRecipientService} from '@service/recipients/default-recipient.service';
import {DefaultRecipientHub} from '@service/recipients/default-recipient-hub';
import {ExternalUserListComponent} from './external-user/external-user-list.component';
import {ExternalUserHub} from '@service/user/external-user-hub';
import {ExternalUserService} from '@service/user/external-user-service';
import {ExternalUserComponent} from './external-user/external-user.component';
import {ConfigurationModule} from '@feature/admin/configuration/configuration.module';

@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule.forChild(adminRoutes),
    FormsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatExpansionModule,
    MatSlideToggleModule,
    SelectionGroupModule,
    ConfigurationModule
  ],
  declarations: [
    AdminComponent,
    AdminNavComponent,
    UserListComponent,
    UserComponent,
    ExternalUserListComponent,
    ExternalUserComponent,
    DefaultAttachmentsComponent,
    DefaultAttachmentComponent,
    DefaultRecipientsComponent,
    RecipientsByTypeComponent
  ],
  providers: [
    DefaultRecipientService,
    DefaultRecipientHub,
    ExternalUserHub,
    ExternalUserService
  ]
})
export class AdminModule {}
