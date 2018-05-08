import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {ContentTypeInterceptor} from './content-type-interceptor';
import {JwtInterceptor} from './jwt-interceptor';


/** Http interceptor providers in outside-in order */
export const httpInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: ContentTypeInterceptor, multi: true },
  { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
];
