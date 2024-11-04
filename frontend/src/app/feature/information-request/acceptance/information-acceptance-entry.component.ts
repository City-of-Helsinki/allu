import {Component, OnInit} from '@angular/core';
import {filter, map, switchMap, take} from 'rxjs/operators';
import {Observable} from 'rxjs';
import { MatLegacyDialog as MatDialog, MatLegacyDialogConfig as MatDialogConfig } from '@angular/material/legacy-dialog';
import {InformationRequestResult} from '@feature/information-request/information-request-result';
import {SetKindsWithSpecifiers} from '@feature/application/actions/application-actions';
import * as InformationRequestResultAction from '@feature/information-request/actions/information-request-result-actions';
import {
  INFORMATION_ACCEPTANCE_MODAL_CONFIG,
  InformationAcceptanceData,
  InformationAcceptanceModalComponent
} from '@feature/information-request/acceptance/information-acceptance-modal.component';
import {select, Store} from '@ngrx/store';
import * as fromAuth from '@feature/auth/reducers';
import {RoleType} from '@model/user/role-type';
import * as fromRoot from '@feature/allu/reducers';
import {ActivatedRoute, Router} from '@angular/router';
import {NumberUtil} from '@util/number.util';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {InformationRequest} from '@app/model/information-request/information-request';

@Component({
  selector: 'information-acceptance-entry',
  template: '<router-outlet></router-outlet>'
})
export class InformationAcceptanceEntryComponent implements OnInit {
  constructor(
    private dialog: MatDialog,
    private store: Store<fromRoot.State>,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.showPendingInfo();
    }, 0);
  }

  private showPendingInfo(): void {
    this.route.data
      .pipe(
        take(1),
        map((routeData: {acceptanceData: InformationAcceptanceData}) => routeData.acceptanceData),
        filter(data => !!data),
        switchMap(data => this.openAcceptanceModal(data))
      ).subscribe((result: InformationRequestResult) => {
        if (result) {
          this.store.dispatch(new SetKindsWithSpecifiers(result.application.kindsWithSpecifiers));
          this.store.dispatch(new InformationRequestResultAction.Save(result));
        }
        this.navigateBack();
      });
  }

  private openAcceptanceModal(data: InformationAcceptanceData): Observable<InformationRequestResult>  {
    return this.createAcceptanceModalConfig(data).pipe(
      switchMap(config => this.dialog.open<InformationAcceptanceModalComponent>(InformationAcceptanceModalComponent, config)
        .afterClosed())
    );
  }

  private createAcceptanceModalConfig(baseData: InformationAcceptanceData): Observable<MatDialogConfig<InformationAcceptanceData>> {
    return this.store.pipe(
      select(fromAuth.getUser),
      filter(user => !!user),
      map(user => user.hasRole(RoleType.ROLE_PROCESS_APPLICATION)),
      map(canProcess => {
        const readonly = !canProcess || this.isClosed(baseData.informationRequest);
        const data = { ...baseData, readonly };
        return {...INFORMATION_ACCEPTANCE_MODAL_CONFIG, data};
      })
    );
  }

  private navigateBack(): void {
    this.route.params.pipe(
      map(params => params['id']),
      map(id => this.getPath(id)),
      take(1)
    ).subscribe(path => this.router.navigate(path, {relativeTo: this.route}));
  }

  private getPath(id: number): string[] {
    return NumberUtil.isDefined(id)
      ? ['../../']
      : ['../'];
  }

  private isClosed(request: InformationRequest): boolean {
    return request
    ? request.status === InformationRequestStatus.CLOSED
    : false;
  }
}
