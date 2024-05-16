import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {MatLegacyCardModule as MatCardModule} from '@angular/material/legacy-card';
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
