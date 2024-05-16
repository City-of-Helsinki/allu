import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA, MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {ChargeBasisEntryForm} from './charge-basis-entry.form';
import {ChargeBasisUnit} from '@model/application/invoice/charge-basis-unit';
import {ChargeBasisEntry} from '@model/application/invoice/charge-basis-entry';
import {NumberUtil} from '@util/number.util';
import {ArrayUtil} from '@util/array-util';
import {ChargeBasisType, manualChargeBasisTypes} from '@model/application/invoice/charge-basis-type';
import {Subject} from 'rxjs';
import {distinctUntilChanged, take, takeUntil} from 'rxjs/internal/operators';
import {FormUtil} from '@util/form.util';

export const CHARGE_BASIS_ENTRY_MODAL_CONFIG = {width: '600PX', data: {}};

@Component({
  selector: 'charge-basis-entry-modal',
  templateUrl: './charge-basis-entry-modal.component.html',
  styleUrls: [
    './charge-basis-entry-modal.component.scss'
  ]
})
export class ChargeBasisEntryModalComponent implements OnInit, OnDestroy {
  chargeBasisEntryForm: UntypedFormGroup;
  chargeBasisTypes = manualChargeBasisTypes;

  typeCtrl: UntypedFormControl;

  private destroy = new Subject<boolean>();

  constructor(public dialogRef: MatDialogRef<ChargeBasisEntryModalComponent>,
              @Inject(MAT_DIALOG_DATA) public data: ChargeBasisEntryModalData,
              private fb: UntypedFormBuilder) {
  }

  ngOnInit(): void {
    this.chargeBasisTypes.sort(ArrayUtil.naturalSortTranslated(['chargeBasis.type'], (item: string) => item));
    const entry = this.data.entry || new ChargeBasisEntry(undefined, ChargeBasisType.DISCOUNT, ChargeBasisUnit.PIECE, 1);
    this.chargeBasisEntryForm = ChargeBasisEntryForm.formGroup(this.fb, entry);
    this.typeCtrl = <UntypedFormControl>this.chargeBasisEntryForm.get('type');

    this.typeCtrl.valueChanges.pipe(
      distinctUntilChanged(),
      takeUntil(this.destroy)
    ).subscribe(type => this.typeChanges(type));

    this.chargeBasisEntryForm.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(entryForm => this.updateNetPrice(entryForm));

    // Mark manual explanation as touched first time user inputs something
    // this marks the control for angulars validation even before user focus leaves the field
    this.chargeBasisEntryForm.get('manualExplanation').valueChanges.pipe(
      take(1)
    ).subscribe(() => this.chargeBasisEntryForm.get('manualExplanation').markAsTouched());
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  onSubmit(): void {
    const entry = ChargeBasisEntryForm.toChargeBasisEntry(this.chargeBasisEntryForm.getRawValue());
    entry.manuallySet = true;
    this.dialogRef.close(entry);
  }

  cancel(): void {
    this.dialogRef.close(undefined);
  }

  private updateNetPrice(form: ChargeBasisEntryForm) {
    if (NumberUtil.isDefined(form.unitPrice) && NumberUtil.isDefined(form.quantity)) {
      this.chargeBasisEntryForm.patchValue({netPrice: form.unitPrice * form.quantity}, {emitEvent: false});
    } else {
      this.chargeBasisEntryForm.patchValue({netPrice: undefined}, {emitEvent: false});
    }
  }

  private typeChanges(typeName: string): void {
    const invoicable = FormUtil.getValue(this.chargeBasisEntryForm, 'invoicable');
    this.chargeBasisEntryForm.get('unitPrice').setValidators([Validators.required]); // this might be disabled by discount component
    this.chargeBasisEntryForm.reset({type: typeName, invoicable});
  }
}

export interface ChargeBasisEntryModalData {
  entry: ChargeBasisEntry;
}
