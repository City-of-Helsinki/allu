import {Directive, Input, TemplateRef, ViewContainerRef} from '@angular/core';
import {CurrentUser} from '../user/current-user';
import {User} from '../../model/common/user';

@Directive({ selector: '[availableTo]' })
export class AvailableToDirective {

  private hasView = false;
  private hasRequiredRole = false;
  private hasRequiredType = true; // Defaults to trues since type parameter is optional

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private currentUser: CurrentUser) {
  }

  @Input() set availableTo(roles: Array<string>) {
    if (roles && roles.length) {
      this.currentUser.user
        .map(u => this.userHasRole(u, roles))
        .subscribe(hasRequiredRole => {
          this.hasRequiredRole = hasRequiredRole;
          this.updateTemplate();
        });
    } else {
      throw new Error('Available to requires at least one role as parameter');
    }
  }

  @Input() set availableToTypes(types: Array<string>) {
    if (types && types.length) {
      this.currentUser.user
        .map(u => this.userHasApplicationType(u, types))
        .subscribe(hasRequiredType => {
          this.hasRequiredType = hasRequiredType;
          this.updateTemplate();
        });
    }
  }

  private userHasRole(user: User, roles: Array<string>): boolean {
    return user.roles.reduce((prev, cur) => prev || roles.some(role => role === cur), false);
  }

  private userHasApplicationType(user: User, types: Array<string>): boolean {
    return user.allowedApplicationTypes.reduce((prev, cur) => prev || types.some(type => type === cur), false);
  }

  private updateTemplate(): void {
    if (this.meetsRequirements() && !this.hasView) {
      this.viewContainer.createEmbeddedView(this.templateRef);
      this.hasView = true;
    } else if (!this.meetsRequirements() && this.hasView) {
      this.viewContainer.clear();
      this.hasView = false;
    }
  }

  private meetsRequirements(): boolean {
    return this.hasRequiredRole && this.hasRequiredType;
  }
}
