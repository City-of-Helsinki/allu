import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges, ViewEncapsulation} from '@angular/core';
import {Application} from '../../../model/application/application';
import {Some} from '../../../util/option';
import {NumberUtil} from '../../../util/number.util';
import {ApplicationIdentifier} from '../../../model/application/application-identifier';
import {ApplicationService} from '../../../service/application/application.service';
import {NotificationService} from '../../../service/notification/notification.service';
import {Router} from '@angular/router';
import {startWith} from 'rxjs/internal/operators';
import {ApplicationStatus} from '../../../model/application/application-status';

@Component({
  selector: 'progressbar',
  templateUrl: './progressbar.component.html',
  styleUrls: [
    './progressbar.component.scss'
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class ProgressbarComponent implements OnChanges {
  @Input() application: Application;
  progress: number;
  replacements: Array<ApplicationIdentifier>;
  existingApplication: boolean;
  selectedApplication: number;

  constructor(private router: Router,
              private service: ApplicationService,
              private notification: NotificationService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    Some(changes.application)
      .map(change => change.currentValue)
      .do(app => {
        this.selectedApplication = app.id;
        this.updateReplacementHistory(app);
        this.progress = this.calculateProgress(app.statusEnum);
        this.existingApplication = NumberUtil.isDefined(app.id);
      });
  }

  showSelected() {
    this.router.navigate(['/applications', this.selectedApplication, 'summary']);
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

      case ApplicationStatus.DECISION:
      case ApplicationStatus.REJECTED:
      case ApplicationStatus.FINISHED:
      case ApplicationStatus.CANCELLED:
      case ApplicationStatus.REPLACED:
      case ApplicationStatus.ARCHIVED: {
        return 100;
      }
      default:
        return  0;
    }
  }

  private updateReplacementHistory(application: Application): void {
    if (NumberUtil.isDefined(application.id)) {
      const defaultReplacements = [
          new ApplicationIdentifier(application.id, application.applicationId, application.identificationNumber)];

      this.service.getReplacementHistory(application.id).pipe(
        startWith(defaultReplacements)
      ).subscribe(
        (replacements) => this.replacements = replacements,
        (err) => this.notification.errorInfo(err));
    } else {
      this.replacements = [];
    }
  }
}
