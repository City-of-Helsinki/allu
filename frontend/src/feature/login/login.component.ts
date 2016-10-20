import {Component, OnInit} from '@angular/core';
import {Router, RouterLink, ActivatedRoute} from '@angular/router';
import { Http, Headers } from '@angular/http';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {UrlUtil} from '../../util/url.util';

@Component({
  selector: 'login',
  template: require('./login.component.html')
})
export class Login implements OnInit {

  private contentHeaders = new Headers();

  constructor(private router: Router, private route: ActivatedRoute, private http: Http, private authHttp: AuthHttp) {
    this.contentHeaders.append('Accept', 'application/json');
    this.contentHeaders.append('Content-Type', 'application/json');
    console.log('Login: ' + router.routerState.snapshot.url);
  }

  login(event, username, password) {
    event.preventDefault();
    let body = JSON.stringify({ 'userName': username });
    this.http.post('/api/auth/login', body, { headers: this.contentHeaders })
      .subscribe(
        response => {
          localStorage.setItem('jwt', response.text());
          this.router.navigateByUrl('/');
        },
        error => {
          alert(error.text());
          console.log(error.text());
        }
      );
  }


  ngOnInit(): void {
    // login page is handling also logout in case user navigates to login page with logout URL
    UrlUtil.urlPathContains(this.route, 'logout').forEach(logout => {
      if (logout) {
        localStorage.removeItem('jwt');
      }
    });
  }
}

