import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormBuilder, FormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {Configuration} from '@model/config/configuration';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Save} from '@feature/admin/configuration/actions/configuration-actions';
import {ConfigurationType} from '@model/config/configuration-type';
import {Some} from '@util/option';
import {MIN_DATE, TimeUtil} from '@util/time.util';
import {takeUntil} from 'rxjs/operators';
import {Subject} from 'rxjs';

const baseValidators = [Validators.required, Validators.min(1)];

@Component({
  selector: 'configuration-calendar-date-value',
  templateUrl: './configuration-calendar-date-value.component.html',
  styleUrls: ['./configuration-value.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigurationCalendarDateValueComponent implements OnInit, OnDestroy {

  @Input() configuration: Configuration;

  form: UntypedFormGroup;

  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(
    private fb: UntypedFormBuilder,
    private store: Store<fromRoot.State>
  ) {}

  ngOnInit(): void {
    const date = Some(this.configuration.value).map(val => TimeUtil.dateFromBackend(val)).orElse(MIN_DATE);

    this.form = this.fb.group({
      day: [date.getDate(), baseValidators],
      month: [TimeUtil.getUiMonth(date), [Validators.required, Validators.min(1), Validators.max(12)]]
    });

    this.form.get('month').valueChanges.pipe(
      takeUntil(this.destroy)
    ).subscribe(month => this.updateDayMaxValidation(month));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  get maxDay(): number {
    const month = this.form.get('month').value;
    return TimeUtil.getEndOfMonth(month);

  }

  submit(): void {
    const formValue = this.form.value;
    const day = formValue.day;
    const month = TimeUtil.fromUiMonth(formValue.month);

    const date = new Date(MIN_DATE.getFullYear(), month, day);

    const configuration: Configuration = {
      ...this.configuration,
      value: TimeUtil.getDateString(date, 'YYYY-MM-DD')
    };
    this.store.dispatch(new Save(configuration));
  }

  private updateDayMaxValidation(month: number): void {
    const maxDay = TimeUtil.getEndOfMonth(month);
    const maxValidator = Validators.max(maxDay);
    const dayCtrl = this.form.get('day');
    dayCtrl.setValidators(baseValidators.concat(maxValidator));
    const currentDay: number = dayCtrl.value;
    if (currentDay > maxDay) {
      dayCtrl.setValue(maxDay);
    }
  }
}
