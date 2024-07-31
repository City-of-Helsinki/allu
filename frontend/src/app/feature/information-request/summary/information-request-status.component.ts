import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {InformationRequestStatus} from '@model/information-request/information-request-status';

type ProgressColor =
  | 'active'
  | 'closed';

@Component({
  selector: 'information-request-status',
  templateUrl: './information-request-status.component.html',
  styleUrls: ['./information-request-status.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InformationRequestStatusComponent implements OnInit {
  @Input() status: InformationRequestStatus;

  progress = 0;
  color: ProgressColor;

  ngOnInit(): void {
    this.progress = this.calculateProgress();
    this.color = this.calculateColor();
  }

  private calculateProgress() {
    switch (this.status) {
      case InformationRequestStatus.DRAFT: {
        return 0;
      }
      case InformationRequestStatus.OPEN: {
        return 30;
      }
      case InformationRequestStatus.RESPONSE_RECEIVED: {
        return 80;
      }
      default: {
        return 100;
      }
    }
  }

  private calculateColor() {
    if (InformationRequestStatus.CLOSED === this.status) {
      return 'closed';
    } else {
      return 'active';
    }
  }
}
