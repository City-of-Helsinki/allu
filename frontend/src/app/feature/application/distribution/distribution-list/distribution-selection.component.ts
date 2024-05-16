import {Component, forwardRef, Input, OnDestroy, OnInit} from '@angular/core';
import {DistributionEntry} from '@model/common/distribution-entry';
import {ControlValueAccessor, UntypedFormBuilder, UntypedFormControl, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Subscription} from 'rxjs';

const DISTRIBUTION_SELECTION_VALUE_ACCESSOR = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => DistributionSelectionComponent),
  multi: true
};

@Component({
  selector: 'distribution-selection',
  templateUrl: './distribution-selection.component.html',
  providers: [DISTRIBUTION_SELECTION_VALUE_ACCESSOR]
})
export class DistributionSelectionComponent implements OnInit, OnDestroy, ControlValueAccessor {
  @Input() distributionList: DistributionEntry[] = [];

  selected: UntypedFormControl;

  private selectedSub: Subscription;

  constructor(private fb: UntypedFormBuilder) {}

  ngOnInit(): void {
    this.selected = this.fb.control([]);

    this.selectedSub = this.selected.valueChanges.subscribe(selected => this._onChange(selected));
  }

  ngOnDestroy(): void {
    this.selectedSub.unsubscribe();
  }


  registerOnChange(fn: any): void {
    this._onChange = fn;
  }

  registerOnTouched(fn: any): void {}

  setDisabledState(isDisabled: boolean): void {
    this.selected.disable({emitEvent: false});
  }

  writeValue(selected: DistributionEntry[] = []): void {
    if (selected) {
      this.selected.patchValue(selected, {emitEvent: false});
    }
  }

  private _onChange = (_: any) => {};
}
