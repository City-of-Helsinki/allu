import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {ToolbarComponent} from './toolbar.component';
import {NavbarComponent} from './navbar/navbar.component';
import {AlluCommonModule} from '../common/allu-common.module';

@NgModule({
  imports: [
    RouterModule.forChild([]),
    AlluCommonModule
  ],
  declarations: [
    ToolbarComponent,
    NavbarComponent
  ],
  exports: [
    ToolbarComponent
  ]
})
export class ToolbarModule {}
