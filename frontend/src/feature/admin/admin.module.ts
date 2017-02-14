import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MdCardModule} from '@angular/material';

import {AlluCommonModule} from '../common/allu-common.module';
import {adminRoutes} from './admin.routing';
import {UserListComponent} from './user/user-list.component';
import {UserComponent} from './user/user.component';
import {AdminComponent} from './admin.component';
import {AdminNavComponent} from './nav/admin-nav.component';
import {DefaultAttachmentsComponent} from './default-attachment/default-attachments.component';
import {DefaultAttachmentComponent} from './default-attachment/default-attachment.component';
import {SelectionGroupModule} from '../common/selection-group/selection-group.module';

@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule.forChild(adminRoutes),
    FormsModule,
    ReactiveFormsModule,
    MdCardModule,
    SelectionGroupModule
  ],
  declarations: [
    AdminComponent,
    AdminNavComponent,
    UserListComponent,
    UserComponent,
    DefaultAttachmentsComponent,
    DefaultAttachmentComponent
  ],
  providers: []
})
export class AdminModule {}
