import {NgModule} from '@angular/core';
import {Login} from './login.component';
import {AuthService} from '../../service/authorization/auth.service';

@NgModule({
  declarations: [
    Login
  ],
  providers: [
    AuthService
  ]
})
export class LoginModule {}
