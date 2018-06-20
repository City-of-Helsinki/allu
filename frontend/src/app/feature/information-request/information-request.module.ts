import {NgModule} from '@angular/core';
import {EffectsModule} from '@ngrx/effects';
import {InformationRequestEffects} from './effects/information-request-effects';
import {InformationRequestService} from '../../service/application/information-request.service';

@NgModule({
  imports: [
    EffectsModule.forFeature([InformationRequestEffects])
  ],
  declarations: [],
  providers: [
    InformationRequestService
  ],
  exports: []
})
export class InformationRequestModule {}
