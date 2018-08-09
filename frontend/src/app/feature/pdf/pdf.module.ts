import {NgModule} from '@angular/core';
import {PdfViewComponent} from '@feature/pdf/pdf-view.component';
import {AlluCommonModule} from '@feature/common/allu-common.module';

@NgModule({
  imports: [
    AlluCommonModule
  ],
  declarations: [
    PdfViewComponent
  ],
  exports: [
    PdfViewComponent
  ],
  providers: []
})
export class PdfModule {}
