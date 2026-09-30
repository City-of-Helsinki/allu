import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {AlluCommonModule} from '../common/allu-common.module';
import {SidebarComponent} from './sidebar.component';

@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule,
    MatCardModule
  ],
  declarations: [
    SidebarComponent
  ],
  exports: [
    SidebarComponent
  ],
  providers: []
})
export class SidebarModule {}
