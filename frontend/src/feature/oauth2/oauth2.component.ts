import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {REDIRECT_URL} from '../../util/local-storage';

/**
 * Landing page component for OAuth2 login.
 * Redirects to url which user tried to navigate before redirection to Oauth page.
 * As default navigate to home.
 */
@Component({
  selector: 'oauth2',
  template: ''
})
export class Oauth2Component implements OnInit {

  constructor(private router: Router) { }

  ngOnInit() {
    const redirectUrl = localStorage.getItem(REDIRECT_URL);
    if (redirectUrl) {
      localStorage.removeItem(REDIRECT_URL);
      this.router.navigateByUrl(redirectUrl);
    } else {
      this.router.navigate(['home']);
    }
  }
}
