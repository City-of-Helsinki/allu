import {Directive, Attribute, ViewContainerRef, DynamicComponentLoader} from '@angular/core';
import {Router, RouterOutlet, ComponentInstruction} from '@angular/router-deprecated';
import {JwtHelper} from 'angular2-jwt/angular2-jwt';

@Directive({
  /* tslint:disable */ selector: 'router-outlet'
})
export class LoginRouterOutlet extends RouterOutlet {
  private parentRouter: Router;
  private jwtHelper: JwtHelper;

  constructor(_viewContainerRef: ViewContainerRef, _loader: DynamicComponentLoader,
              _parentRouter: Router, @Attribute('name') nameAttr: string) {
    super(_viewContainerRef, _loader, _parentRouter, nameAttr);

    this.parentRouter = _parentRouter;
    this.jwtHelper = new JwtHelper;
  }

  activate(instruction: ComponentInstruction) {
    let url = instruction.urlPath;
    let jwt = localStorage.getItem('jwt');
    if ('login' !== url && (!jwt || this.jwtHelper.isTokenExpired(jwt))) {
      // todo: redirect to Login, may be there a better way?
      this.parentRouter.navigateByUrl('/login');
    }
    return super.activate(instruction);
  }
}
