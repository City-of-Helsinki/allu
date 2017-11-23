import {Component, Input, OnInit, OnChanges, SimpleChanges, ChangeDetectionStrategy} from '@angular/core';
import {Application} from '../../../model/application/application';
import {ProgressStep} from './progress-step';

@Component({
  selector: 'progressbar',
  templateUrl: './progressbar.component.html',
  styleUrls: [
    './progressbar.component.scss'
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgressbarComponent implements OnInit, OnChanges {
  @Input() step: number;
  @Input() application: Application;
  width: number;
  identifier: string;
  status: string;

  constructor() {}

  ngOnInit() {
    this.width = this.calculateWidth(this.step);
    this.status = this.application ? this.application.status : undefined;
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.identifier = this.hasId(this.application) ? this.application.applicationId : 'UUSI HAKEMUS';
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

  private hasId(application: Application): boolean {
    return !!application && !!application.id;
  }
}
