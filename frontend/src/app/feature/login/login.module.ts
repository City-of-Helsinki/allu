import {NgModule} from '@angular/core';
import {LoginComponent} from './login.component';
import {AuthService} from '../../service/authorization/auth.service';
import {AlluCommonModule} from '../common/allu-common.module';

@NgModule({
  imports: [
    AlluCommonModule
  ],
  declarations: [
    LoginComponent
  ],
  providers: [
    AuthService
  ]
})
export class LoginModule {}
