import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AlluCommonModule} from '../../common/allu-common.module';
import {TermsComponent} from './terms.component';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule
  ],
  declarations: [
    TermsComponent
  ],
  providers: [
  ],
  exports: [
    TermsComponent
  ]
})
export class TermsModule {}
