import {NgModule} from '@angular/core';
import {EffectsModule} from '@ngrx/effects';
import {InformationRequestEffects} from './effects/information-request-effects';
import {InformationRequestService} from '../../service/application/information-request.service';
import {MatDialogModule} from '@angular/material';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '../common/allu-common.module';
import {InformationAcceptanceModalComponent} from './acceptance/information-acceptance-modal.component';
import {FieldAcceptanceComponent} from './acceptance/field-acceptance.component';
import {FieldGroupAcceptanceComponent} from './acceptance/field-group-acceptance.component';
import {CustomerAcceptanceComponent} from './acceptance/customer-acceptance.component';
import {FieldValueComponent} from './acceptance/field-value.component';
import {StoreModule} from '@ngrx/store';
import {reducers} from './reducers';

@NgModule({
  imports: [
    StoreModule.forFeature('informationRequest', reducers),
    EffectsModule.forFeature([InformationRequestEffects]),
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    AlluCommonModule
  ],
  declarations: [
    InformationAcceptanceModalComponent,
    FieldValueComponent,
    FieldAcceptanceComponent,
    FieldGroupAcceptanceComponent,
    CustomerAcceptanceComponent
  ],
  providers: [
    InformationRequestService
  ],
  exports: [
    InformationAcceptanceModalComponent
  ],
  entryComponents: [
    InformationAcceptanceModalComponent
  ]
})
export class InformationRequestModule {}
