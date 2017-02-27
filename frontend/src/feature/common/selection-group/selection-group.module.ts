import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';

import {SelectionGroupComponent} from './selection-group.component';
import {SelectionItemComponent} from './selection-item.component';
import {AlluCommonModule} from '../allu-common.module';

@NgModule({
  imports: [
    AlluCommonModule,
    FormsModule
  ],
  declarations: [
    SelectionGroupComponent,
    SelectionItemComponent
  ],
  exports: [
    SelectionGroupComponent,
    SelectionItemComponent
  ]
})
export class SelectionGroupModule {}
