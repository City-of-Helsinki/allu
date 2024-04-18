import {Component, EventEmitter, forwardRef, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {ControlValueAccessor, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, NG_VALUE_ACCESSOR, Validators} from '@angular/forms';
import {StringUtil} from '@util/string.util';
import {Subject} from 'rxjs/internal/Subject';
import {takeUntil} from 'rxjs/operators';

const REQUEST_FIELD_VALUE_ACCESSOR = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => RequestFieldComponent),
  multi: true
};

@Component({
  selector: 'request-field',
  templateUrl: './request-field.component.html',
  styleUrls: ['./request-field.component.scss'],
  providers: [REQUEST_FIELD_VALUE_ACCESSOR]
})
export class RequestFieldComponent implements OnInit, OnDestroy, ControlValueAccessor {
  @Input() label: string;
  @Output() selectedChange = new EventEmitter<boolean>();

  selectedCtrl: UntypedFormControl;
  descriptionCtrl: UntypedFormControl;
  private form: UntypedFormGroup;


  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(private fb: UntypedFormBuilder) {}

  ngOnInit() {
    this.selectedCtrl = this.fb.control(false);
    this.descriptionCtrl = this.fb.control('');
    this.form = this.fb.group({
      selected: this.selectedCtrl,
      description: this.descriptionCtrl
    });

    this.selectedCtrl.valueChanges.pipe(
      takeUntil(this.destroy)
    ).subscribe(selected => this.onSelectedChange(selected));

    this.descriptionCtrl.valueChanges.pipe(
      takeUntil(this.destroy)
    ).subscribe(desc => this.onDescriptionChange(desc));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  checked(): boolean {
    return this.selectedCtrl.value;
  }

  registerOnChange(fn: any): void {
    this._onChange = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    if (isDisabled) {
      this.form.disable();
    } else {
      this.form.enable();
    }
  }

  registerOnTouched(fn: any): void {
    this._onTouched = fn;
  }

  writeValue(description: string): void {
    this.descriptionCtrl.setValue(description);
    this.selectedCtrl.patchValue(!StringUtil.isEmpty(description), {emitEvent: false});
  }

  private onSelectedChange(selected: boolean): void {
    if (!selected) {
      this.descriptionCtrl.reset();
    }

    this.selectedChange.emit(selected);
  }

  private onDescriptionChange(description: string): void {
    this._onChange(description);
  }

  private _onChange = (_: any) => {};

  private _onTouched = (_: any) => {};
}
