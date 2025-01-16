import {Routes} from '@angular/router';

import {UserListComponent} from '../admin/user/user-list.component';
import {UserComponent} from './user/user.component';
import {AdminComponent} from './admin.component';
import {DefaultAttachmentsComponent} from './default-attachment/default-attachments.component';
import {DefaultAttachmentComponent} from './default-attachment/default-attachment.component';
import {DefaultRecipientsComponent} from './default-recipients/default-recipients.component';
import {ExternalUserListComponent} from './external-user/external-user-list.component';
import {ExternalUserComponent} from './external-user/external-user.component';
import {ConfigurationComponent} from '@feature/admin/configuration/configuration.component';
import {AdminGuard} from '@app/service/authorization/admin-guard.service';
import { PruneDataComponent } from './prune-data/prune-data.component';


const attachmentChildRoutes = [
  { path: '', component: DefaultAttachmentsComponent },
  { path: 'new', component: DefaultAttachmentComponent},
  { path: ':id', component: DefaultAttachmentComponent }
];

export const adminRoutes: Routes = [
  { path: 'admin', component: AdminComponent, canActivate: [AdminGuard], children: [
    { path: '', redirectTo: 'users', pathMatch: 'full'},
    { path: 'users', children: [
      { path: '', component: UserListComponent },
      { path: 'new', component: UserComponent },
      { path: ':id', component: UserComponent },
    ]},
    { path: 'external-users', children: [
      { path: '', component: ExternalUserListComponent },
      { path: 'new', component: ExternalUserComponent },
      { path: ':id', component: ExternalUserComponent }
    ]},
    { path: 'default-attachments', data: {attachmentType: 'DEFAULT'}, children: attachmentChildRoutes },
    { path: 'default-images', data: {attachmentType: 'DEFAULT_IMAGE'}, children: attachmentChildRoutes },
    { path: 'default-recipients', component: DefaultRecipientsComponent},
    { path: 'prune-data', children: [
     { path: '', redirectTo: 'excavation_announcement', pathMatch: 'full' },
     { path: ':tab', component: PruneDataComponent}
    ]},
    { path: 'configuration', component: ConfigurationComponent }
  ]}
];
