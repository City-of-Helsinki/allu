import {Directive, Input, TemplateRef, ViewContainerRef} from '@angular/core';
import {CurrentUser} from '../user/current-user';

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
      this.currentUser.hasRole(roles)
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
      this.currentUser.hasApplicationType(types)
        .subscribe(hasRequiredType => {
          this.hasRequiredType = hasRequiredType;
          this.updateTemplate();
        });
    }
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
