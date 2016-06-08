import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router-deprecated';
import { CORE_DIRECTIVES, FORM_DIRECTIVES } from '@angular/common';
import { Http, Headers } from '@angular/http';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {CustomerMapper} from '../../service/mapper/customer-mapper';

@Component({
  selector: 'login',
  directives: [RouterLink, CORE_DIRECTIVES, FORM_DIRECTIVES ],
  template: require('./login.component.html')
})
export class Login {

  private contentHeaders = new Headers();

  constructor(private router: Router, private http: Http, private authHttp: AuthHttp) {
    this.contentHeaders.append('Accept', 'application/json');
    this.contentHeaders.append('Content-Type', 'application/json');
  }

  login(event, username, password) {
    event.preventDefault();
    let body = JSON.stringify({ username, password });
    this.http.post('/api/auth/login', body, { headers: this.contentHeaders })
      .subscribe(
        response => {
          localStorage.setItem('jwt', response.text());
          this.router.parent.navigateByUrl('/');
        },
        error => {
          alert(error.text());
          console.log(error.text());
        }
      );
  }

  testAuthHttp(): void {
    console.log('clicko!');
    // console.log('Customer',
    //   JSON.stringify(new Customer(undefined, 'appName', undefined, undefined, undefined, undefined, undefined, undefined, undefined)));

    this.authHttp.get('/api/applications/15')
      .subscribe(
        (data) => {
          console.log('parsed JSON', JSON.parse(data.text()));
          let jsonCustomer = JSON.parse(data.text()).applicationList[0].customer;
          console.log('got data!', jsonCustomer);
          let customer = CustomerMapper.mapBackend(jsonCustomer);
          console.log(customer);
        },
            err => console.log(err),
            () => console.log('Request Complete')
          );
    }
}

