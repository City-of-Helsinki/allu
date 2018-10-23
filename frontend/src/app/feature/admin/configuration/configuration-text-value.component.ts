import {Component, forwardRef, Input, OnDestroy, OnInit} from '@angular/core';
import {ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';

const CONFIGURATION_TEXT_VALUE_ACCESSOR = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => ConfigurationTextValueComponent),
  multi: true
};

@Component({
  selector: 'configuration-text-value',
  templateUrl: './configuration-text-value.component.html',
  styleUrls: [],
  providers: [CONFIGURATION_TEXT_VALUE_ACCESSOR]
})
export class ConfigurationTextValueComponent implements ControlValueAccessor, OnInit, OnDestroy {

  @Input() placeholder: string;

  valueCtrl: FormControl = new FormControl('', Validators.required);

  private valueSub: Subscription;

  ngOnInit(): void {
    this.valueSub = this.valueCtrl.valueChanges.subscribe(val => this._onChange(val));
  }

  ngOnDestroy(): void {
    this.valueSub.unsubscribe();
  }

  registerOnChange(fn: any): void {
    this._onChange = fn;
  }

  registerOnTouched(fn: any): void {}

  writeValue(value: string): void {
    this.valueCtrl.setValue(value);
  }

  private _onChange = (_: any) => {};
}
