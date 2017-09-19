import {NgModule} from '@angular/core';
import {Login} from './login.component';
import {AuthService} from '../../service/authorization/auth.service';
import {AlluCommonModule} from '../common/allu-common.module';

@NgModule({
  imports: [
    AlluCommonModule
  ],
  declarations: [
    Login
  ],
  providers: [
    AuthService
  ]
})
export class LoginModule {}
