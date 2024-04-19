import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {ToolbarComponent} from './toolbar.component';
import {NavbarComponent} from './navbar/navbar.component';
import {AlluCommonModule} from '../common/allu-common.module';
import {ApplicationBasketModule} from '../project/application-basket/application-basket.module';
import {MatLegacyChipsModule as MatChipsModule} from '@angular/material/legacy-chips';

@NgModule({
  imports: [
    RouterModule.forChild([]),
    AlluCommonModule,
    MatChipsModule,
    ApplicationBasketModule
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
