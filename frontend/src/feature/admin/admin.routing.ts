import {Routes} from '@angular/router';

import {UserListComponent} from '../admin/user/user-list.component';
import {UserComponent} from './user/user.component';
import {AdminComponent} from './admin.component';
import {DefaultAttachmentsComponent} from './default-attachment/default-attachments.component';
import {DefaultAttachmentComponent} from './default-attachment/default-attachment.component';

const attachmentChildRoutes = [
  { path: '', component: DefaultAttachmentsComponent },
  { path: 'new', component: DefaultAttachmentComponent},
  { path: ':id', component: DefaultAttachmentComponent }
];

export const adminRoutes: Routes = [
  { path: 'admin', component: AdminComponent, children: [
    { path: '', redirectTo: 'user-list', pathMatch: 'full'},
    { path: 'user-list', component: UserListComponent },
    { path: 'user', component: UserComponent },
    { path: 'user/:userName', component: UserComponent },
    { path: 'default-attachments', data: {attachmentType: 'DEFAULT'}, children: attachmentChildRoutes },
    { path: 'default-images', data: {attachmentType: 'DEFAULT_IMAGE'}, children: attachmentChildRoutes }
  ]}
];
