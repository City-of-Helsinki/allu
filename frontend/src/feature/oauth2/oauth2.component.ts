import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

/**
 * Landing page component for OAuth2 login. Simply
 * redirects to home.
 */
@Component({
  selector: 'oauth2',
  template: ''
})
export class Oauth2Component implements OnInit {

  constructor(private router: Router) { }

  ngOnInit() {
    this.router.navigate(['home']);
  }
}
