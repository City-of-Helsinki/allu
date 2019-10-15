import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {RecurringType, recurringTypeFromDate} from '@feature/application/info/recurring/recurring-type';

@Component({
  selector: 'validity-end-time',
  templateUrl: './validity-end-time.component.html',
  styleUrls: ['validity-end-time.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ValidityEndTimeComponent implements OnInit {
  @Input() startTime: Date;
  @Input() endTime: Date;
  @Input() recurringEndTime: Date;

  recurringType: RecurringType;

  ngOnInit(): void {
    this.recurringType = recurringTypeFromDate(this.recurringEndTime);
  }
}
