import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges, ViewEncapsulation} from '@angular/core';
import {Application} from '../../../model/application/application';
import {ProgressStep} from './progress-step';
import {Some} from '../../../util/option';
import {NumberUtil} from '../../../util/number.util';
import {ApplicationIdentifier} from '../../../model/application/application-identifier';
import {ApplicationService} from '../../../service/application/application.service';
import {NotificationService} from '../../../service/notification/notification.service';
import {Router} from '@angular/router';

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
  @Input() step: number;
  @Input() application: Application;
  width: number;
  replacements: Array<ApplicationIdentifier>;
  selectedApplication: number;

  constructor(private router: Router,
              private service: ApplicationService,
              private notification: NotificationService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    Some(changes.step)
      .map(change => change.currentValue)
      .map(step => this.calculateWidth(step))
      .do(width => this.width = width);

    Some(changes.application)
      .map(change => change.currentValue)
      .do(app => {
        this.selectedApplication = app.id;
        this.updateReplacementHistory(app);
      });
  }

  get existingApplication(): boolean {
    return Some(this.application)
      .map(app => NumberUtil.isDefined(app.id))
      .orElse(false);
  }

  showSelected() {
    this.router.navigate(['/applications', this.selectedApplication, 'summary']);
  }

  private calculateWidth(step: ProgressStep): number {
    switch (step) {
      case ProgressStep.LOCATION:
        return 9;
      case ProgressStep.INFORMATION:
        return 25;
      case ProgressStep.SUMMARY:
        return 42;
      case ProgressStep.HANDLING:
        return 58;
      case ProgressStep.DECISION:
        return 75;
      default:
        return  0;
    }
  }

  private updateReplacementHistory(application: Application): void {
    if (NumberUtil.isDefined(application.id)) {
      const defaultReplacements = [
          new ApplicationIdentifier(application.id, application.applicationId, application.identificationNumber)];

      this.service.getReplacementHistory(application.id)
        .startWith(defaultReplacements)
        .subscribe(
          (replacements) => this.replacements = replacements,
          (err) => this.notification.errorInfo(err));
    } else {
      this.replacements = [];
    }
  }
}
