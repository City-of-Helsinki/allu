import {Component, Input, OnInit} from '@angular/core';

export enum ProgressStep {
  LOCATION,
  INFORMATION,
  SUMMARY,
  HANDLING,
  DECISION,
  MONITORING
}

export enum ProgressMode {
  NEW,
  EDIT
}

@Component({
  selector: 'progressbar',
  template: require('./progressbar.component.html'),
  styles: [
    require('./progressbar.component.scss')
  ]
})
export class ProgressbarComponent implements OnInit {
  @Input() step: number;
  @Input() mode: number;
  private width: number;
  private text: string;

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

    switch (this.mode) {
      case ProgressMode.NEW:
        this.text = 'UUSI HAKEMUS';
        break;
      case ProgressMode.EDIT:
        this.text = 'MUOKKAA HAKEMUSTA';
        break;
      default:
        this.text = 'EI ASETETTU';
        break;

    }
  }
}
