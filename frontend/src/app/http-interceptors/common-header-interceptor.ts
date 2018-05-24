import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable()
export class CommonHeaderInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const headers = req.clone({
      setHeaders: {
        'Content-Type': 'application/json',
        'Accept-Language': 'fi-FI' }
    });

    return next.handle(headers);
  }
}
