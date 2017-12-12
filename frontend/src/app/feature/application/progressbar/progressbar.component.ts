import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {Application} from '../../../model/application/application';
import {ProgressStep} from './progress-step';
import {Some} from '../../../util/option';
import {findTranslation} from '../../../util/translations';
import {NumberUtil} from '../../../util/number.util';

@Component({
  selector: 'progressbar',
  templateUrl: './progressbar.component.html',
  styleUrls: [
    './progressbar.component.scss'
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgressbarComponent implements OnInit {
  @Input() step: number;
  @Input() application: Application;
  width: number;
  identifier: string;
  status: string;

  constructor() {}

  ngOnInit() {
    this.width = this.calculateWidth(this.step);
    this.status = this.application ? this.application.status : undefined;
    this.identifier = Some(this.application)
      .map(app => app.applicationId)
      .orElse(findTranslation('application.newApplication'));
  }

  get existingApplication(): boolean {
    return Some(this.application)
      .map(app => NumberUtil.isDefined(app.id))
      .orElse(false);
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
}
