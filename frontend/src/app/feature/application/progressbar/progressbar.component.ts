/* eslint-disable no-duplicate-case */
// No point in modifying already working flow
import {ChangeDetectionStrategy, Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {Application} from '@model/application/application';
import {NumberUtil} from '@util/number.util';
import {ApplicationIdentifier} from '@model/application/application-identifier';
import {ApplicationService} from '@service/application/application.service';
import {Router} from '@angular/router';
import {ApplicationStatus} from '@model/application/application-status';
import {Observable} from 'rxjs/index';
import {map} from 'rxjs/internal/operators';
import {select, Store} from '@ngrx/store';
import * as fromApplication from '@feature/application/reducers';
import {Add} from '@feature/project/actions/application-basket-actions';
import {getByIdentifier} from '@feature/common/external-url-config';

export type ProgressColor =
  | 'pending'
  | 'decision'
  | 'finished'
  | 'rejected'
  | 'cancelled'
  | 'history';

@Component({
  selector: 'progressbar',
  templateUrl: './progressbar.component.html',
  styleUrls: [
    './progressbar.component.scss'
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class ProgressbarComponent implements OnInit {
  progress: number;
  color: ProgressColor;
  replacements$: Observable<ApplicationIdentifier[]>;
  hasReplacements$: Observable<boolean>;
  existingApplication: boolean;
  externalUrl: string;

  private _application: Application = new Application();

  constructor(private router: Router,
              private service: ApplicationService,
              private store: Store<fromApplication.State>) {
  }

  ngOnInit(): void {
    this.replacements$ = this.store.pipe(select(fromApplication.getReplacementHistory));
    this.hasReplacements$ = this.replacements$.pipe(map(replacements => replacements.length > 1));
  }

  @Input()
  get application() { return this._application; }
  set application(application: Application) {
    this._application = application;
    this.progress = this.calculateProgress(application.status);
    this.color = this.calculateColor(application.status);
    this.existingApplication = NumberUtil.isDefined(application.id);
    this.externalUrl = getByIdentifier(application.identificationNumber);
  }

  show(id: number) {
    this.router.navigate(['/applications', id, 'summary']);
  }

  addToBasket(applicationId: number): void {
    this.store.dispatch(new Add(applicationId));
  }

  private calculateProgress(status: ApplicationStatus): number {
    switch (status) {
      case ApplicationStatus.PENDING_CLIENT:
      case ApplicationStatus.PRE_RESERVED: {
        return 0;
      }

      case ApplicationStatus.PENDING:
      case ApplicationStatus.WAITING_INFORMATION:
      case ApplicationStatus.INFORMATION_RECEIVED: {
        return 25;
      }

      case ApplicationStatus.HANDLING:
      case ApplicationStatus.RETURNED_TO_PREPARATION: {
        return 50;
      }

      case ApplicationStatus.WAITING_CONTRACT_APPROVAL:
      case ApplicationStatus.DECISIONMAKING: {
        return 75;
      }

      case ApplicationStatus.NOTE:
      case ApplicationStatus.DECISION:
      case ApplicationStatus.OPERATIONAL_CONDITION:
      case ApplicationStatus.REJECTED:
      case ApplicationStatus.FINISHED:
      case ApplicationStatus.CANCELLED:
      case ApplicationStatus.REPLACED:
      case ApplicationStatus.ARCHIVED:
      case ApplicationStatus.TERMINATED: {
        return 100;
      }
      default:
        return  0;
    }
  }

  private calculateColor(status: ApplicationStatus): ProgressColor {
    switch (status) {
      case ApplicationStatus.DECISION:
      case ApplicationStatus.OPERATIONAL_CONDITION:
      case ApplicationStatus.TERMINATED: {
        return 'decision';
      }

      case ApplicationStatus.REJECTED: {
        return 'rejected';
      }

      case ApplicationStatus.CANCELLED: {
        return 'cancelled';
      }

      case ApplicationStatus.NOTE:
      case ApplicationStatus.FINISHED: {
        return 'finished';
      }

      case ApplicationStatus.REPLACED:
      case ApplicationStatus.ARCHIVED:
      case ApplicationStatus.TERMINATED: {
        return 'history';
      }
      default:
        return 'pending';
    }
  }
}
