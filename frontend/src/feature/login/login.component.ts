import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {UrlUtil} from '../../util/url.util';
import {AuthService} from './auth.service';

@Component({
  selector: 'login',
  template: require('./login.component.html')
})
export class Login implements OnInit {


  constructor(private router: Router, private route: ActivatedRoute, private authentication: AuthService) {
    console.log('Login: ' + router.routerState.snapshot.url);
  }

  login(event, username) {
    event.preventDefault();
    this.authentication.login(username).subscribe(
      response => this.router.navigateByUrl('/'),
      error => {
        alert(error.text());
        console.log(error.text());
      });
  }


  ngOnInit(): void {
    // login page is handling also logout in case user navigates to login page with logout URL
    UrlUtil.urlPathContains(this.route, 'logout')
      .filter(logout => logout)
      .subscribe(logout => this.authentication.logout());
  }
}

