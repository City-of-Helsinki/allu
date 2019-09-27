import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {Application} from '@model/application/application';
import {recurringTypeFromDate, RecurringType} from '@feature/application/info/recurring/recurring-type';

@Component({
  selector: 'validity-time',
  templateUrl: './validity-time.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ValidityTimeComponent implements OnInit {
  @Input() application: Application;
  @Input() terminationDate: Date;

  recurringType: RecurringType;

  ngOnInit(): void {
    this.recurringType = recurringTypeFromDate(this.application.recurringEndTime);
  }
}
