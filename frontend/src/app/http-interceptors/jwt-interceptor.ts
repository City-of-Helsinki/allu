import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {AuthService} from '../service/authorization/auth.service';

const allowedRoutes = ['/api'];
const anonymousRoutes = ['/api/oauth2/', '/api/uiconfig', '/api/auth/login'];

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let nextReq = req;
    if (this.requireToken(req.url) && this.authService.token) {
      const token = this.authService.token;
      nextReq = req.clone({setHeaders: {Authorization: this.bearer(token)}});
    }
    return next.handle(nextReq);
  }

  private requireToken(url: string): boolean {
    return this.allowed(url) && !this.anonymous(url);
  }

  private anonymous(url: string): boolean {
    return anonymousRoutes.some(route => url.indexOf(route) >= 0);
  }

  private allowed(url: string): boolean {
    return allowedRoutes.some(route => url.indexOf(route) >= 0);
  }

  private bearer(token: string): string {
    return `Bearer ${token}`;
  }
}
