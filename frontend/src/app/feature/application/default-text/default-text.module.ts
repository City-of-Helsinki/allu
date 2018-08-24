import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AlluCommonModule} from '../../common/allu-common.module';
import {DefaultTextComponent} from './default-text.component';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule
  ],
  declarations: [
    DefaultTextComponent
  ],
  providers: [
  ],
  exports: [
    DefaultTextComponent
  ]
})
export class DefaultTextModule {}
