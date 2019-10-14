import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {recurringTypeFromDate, RecurringType} from '@feature/application/info/recurring/recurring-type';

@Component({
  selector: 'validity-time',
  templateUrl: './validity-time.component.html',
  styleUrls: ['./validity-time.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ValidityTimeComponent implements OnInit {
  @Input() startTime: Date;
  @Input() endTime: Date;
  @Input() recurringEndTime: Date;
  @Input() terminationDate: Date;

  recurringType: RecurringType;

  ngOnInit(): void {
    this.recurringType = recurringTypeFromDate(this.recurringEndTime);
  }
}
