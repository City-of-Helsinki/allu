import {Component, forwardRef, Input, OnDestroy, OnInit} from '@angular/core';
import {ControlValueAccessor, FormBuilder, FormControl, FormGroup, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';
import {ComplexValidator} from '../../../../util/complex-validator';
import {MAX_YEAR, MIN_YEAR} from '../../../../util/time.util';

type RecurringType = 'none' | 'forNow' | 'until';

const RECURRING_VALUE_ACCESSOR = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => RecurringComponent),
  multi: true
};

@Component({
  selector: 'recurring',
  viewProviders: [],
  templateUrl: './recurring.component.html',
  styleUrls: [
    './recurring.component.scss'
  ],
  providers: [RECURRING_VALUE_ACCESSOR]
})
export class RecurringComponent implements OnInit, OnDestroy, ControlValueAccessor {
  @Input() readonly = false;
  recurringForm: FormGroup;
  minYear = MIN_YEAR;
  maxYear = MAX_YEAR;

  private recurringTypeCtrl: FormControl;
  private endYearCtrl: FormControl;
  private endYearSubscription: Subscription;
  private recurringTypeSubscription: Subscription;

  constructor(private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.endYearCtrl = this.fb.control(undefined, ComplexValidator.betweenOrEmpty(MIN_YEAR, MAX_YEAR));
    this.recurringTypeCtrl = this.fb.control('none');
    this.recurringForm = this.fb.group({
      type: this.recurringTypeCtrl,
      endYear: this.endYearCtrl
    });

    this.recurringTypeSubscription = this.recurringTypeCtrl.valueChanges.subscribe(type => this.typeChange(type));
    this.endYearSubscription = this.endYearCtrl.valueChanges.subscribe(endYear => this._onChange(endYear));

    if (this.readonly) {
      this.recurringForm.disable();
    }
  }

  ngOnDestroy(): void {
    this.recurringTypeSubscription.unsubscribe();
    this.endYearSubscription.unsubscribe();
  }

  writeValue(endYear: number): void {
    if (!endYear) {
      this.recurringTypeCtrl.patchValue('none');
    } else if (endYear === MAX_YEAR) {
      this.recurringTypeCtrl.patchValue('forNow');
    } else {
      this.recurringTypeCtrl.patchValue('until');
      this.endYearCtrl.patchValue(endYear);
    }
  }

  registerOnChange(fn: any): void {
    this._onChange = fn;
  }

  registerOnTouched(fn: any): void {}

  private _onChange = (_: any) => {};

  private typeChange(type: RecurringType): void {
    if (type !== 'until') {
      this.endYearCtrl.reset();
    }

    if (type === 'forNow') {
      this._onChange(MAX_YEAR);
    }
  }
}

