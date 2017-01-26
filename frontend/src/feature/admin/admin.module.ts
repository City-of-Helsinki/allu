import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MdCardModule} from '@angular/material';

import {AlluCommonModule} from '../common/allu-common.module';
import {adminRoutes} from './admin.routing';
import {UserListComponent} from './user/user-list.component';
import {UserComponent} from './user/user.component';
import {CommaSeparatedPipe} from '../../pipe/comma-separated.pipe';

@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule.forChild(adminRoutes),
    FormsModule,
    ReactiveFormsModule,
    MdCardModule
  ],
  declarations: [
    UserListComponent,
    UserComponent
  ],
  providers: []
})
export class AdminModule {}
