import {Component, forwardRef, Input, OnDestroy, OnInit} from '@angular/core';
import {ControlValueAccessor, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, NG_VALUE_ACCESSOR} from '@angular/forms';
import {ComplexValidator} from '@util/complex-validator';
import {MAX_YEAR, MIN_YEAR} from '@util/time.util';
import {Subject} from 'rxjs/internal/Subject';
import {takeUntil} from 'rxjs/operators';
import {RecurringType} from '@feature/application/info/recurring/recurring-type';

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
  @Input() forNowSelectable = true;
  recurringForm: UntypedFormGroup;
  minYear = MIN_YEAR;
  maxYear = MAX_YEAR;

  private recurringTypeCtrl: UntypedFormControl;
  private endYearCtrl: UntypedFormControl;

  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(private fb: UntypedFormBuilder) {
  }

  ngOnInit(): void {
    this.endYearCtrl = this.fb.control(undefined, ComplexValidator.betweenOrEmpty(MIN_YEAR, MAX_YEAR));
    this.recurringTypeCtrl = this.fb.control('none');
    this.recurringForm = this.fb.group({
      type: this.recurringTypeCtrl,
      endYear: this.endYearCtrl
    });

    this.recurringTypeCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(type => this.typeChange(type));

    this.endYearCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(endYear => this._onChange(endYear));

    if (this.readonly) {
      this.recurringForm.disable();
    }
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  writeValue(endYear: number): void {
    if (!endYear) {
      this.recurringTypeCtrl.patchValue(RecurringType.NONE);
    } else if (endYear === MAX_YEAR) {
      this.recurringTypeCtrl.patchValue(RecurringType.FOR_NOW);
    } else {
      this.recurringTypeCtrl.patchValue(RecurringType.UNTIL);
      this.endYearCtrl.patchValue(endYear);
    }
  }

  registerOnChange(fn: any): void {
    this._onChange = fn;
  }

  registerOnTouched(fn: any): void {}

  private _onChange = (_: any) => {};

  private typeChange(type: RecurringType): void {
    if (type !== RecurringType.UNTIL) {
      this.endYearCtrl.reset();
    }

    if (type === RecurringType.FOR_NOW) {
      this._onChange(MAX_YEAR);
    }
  }
}

