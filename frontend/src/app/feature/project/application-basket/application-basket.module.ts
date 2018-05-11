import {NgModule} from '@angular/core';
import {AlluCommonModule} from '../../common/allu-common.module';
import {ApplicationBasketComponent} from './application-basket.component';
import {RouterModule} from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([]),
    AlluCommonModule
  ],
  declarations: [
    ApplicationBasketComponent
  ],
  exports: [
    ApplicationBasketComponent
  ]
})
export class ApplicationBasketModule {}
