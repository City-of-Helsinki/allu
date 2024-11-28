import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatLegacyCardModule as MatCardModule} from '@angular/material/legacy-card';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatLegacyPaginatorModule as MatPaginatorModule} from '@angular/material/legacy-paginator';
import {MatLegacySlideToggleModule as MatSlideToggleModule} from '@angular/material/legacy-slide-toggle';
import {MatSortModule} from '@angular/material/sort';
import {MatLegacyTableModule as MatTableModule} from '@angular/material/legacy-table';

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
import { PruneApplicationsComponent } from './prune-applications/prune-applications.component';

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
    ConfigurationModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule
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
    RecipientsByTypeComponent,
    PruneApplicationsComponent
  ],
  providers: [
    DefaultRecipientService,
    DefaultRecipientHub,
    ExternalUserHub,
    ExternalUserService
  ]
})
export class AdminModule {}
