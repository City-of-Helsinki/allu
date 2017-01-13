import {Component, Input, OnInit, OnChanges, SimpleChanges, ChangeDetectionStrategy} from '@angular/core';
import {Application} from '../../../model/application/application';

export enum ProgressStep {
  LOCATION,
  INFORMATION,
  SUMMARY,
  HANDLING,
  DECISION,
  MONITORING
}

@Component({
  selector: 'progressbar',
  template: require('./progressbar.component.html'),
  styles: [
    require('./progressbar.component.scss')
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgressbarComponent implements OnInit, OnChanges {
  @Input() step: number;
  @Input() application: Application;
  width: number;
  text: string;

  constructor() {}

  ngOnInit() {
    switch (this.step) {
      case ProgressStep.LOCATION:
        this.width = 9;
        break;
      case ProgressStep.INFORMATION:
        this.width = 25;
        break;
      case ProgressStep.SUMMARY:
        this.width = 42;
        break;
      case ProgressStep.DECISION:
        this.width = 75;
        break;
      default:
        this.width = 0;
        break;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.text = this.hasId(this.application) ? this.application.applicationId : 'UUSI HAKEMUS';
  }

  private hasId(application: Application): boolean {
    return !!application && !!application.id;
  }
}
