import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';
import {DistributionType} from '@model/common/distribution-type';
import {EnumUtil} from '@util/enum.util';
import {DistributionEntry} from '@model/common/distribution-entry';
import {postalCodeValidator} from '@util/complex-validator';
import {PostalAddress} from '@model/common/postal-address';
import {FormUtil} from '@util/form.util';

interface DistributionEntryForm {
  id?: number;
  name?: string;
  type?: string;
  email?: string;
  streetAddress?: string;
  postalCode?: string;
  city?: string;
  edit?: boolean;
}

@Component({
  selector: 'distribution-list',
  templateUrl: './distribution-list.component.html',
  styleUrls: [
    './distribution-list.component.scss'
  ]
})
export class DistributionListComponent implements OnInit {
  @Input() readonly: boolean;

  @Output() distributionChange: EventEmitter<DistributionEntry[]> = new EventEmitter<DistributionEntry[]>();

  form: UntypedFormGroup;
  distributionRows: UntypedFormArray;
  distributionTypes = EnumUtil.enumValues(DistributionType);

  constructor(private fb: UntypedFormBuilder) {
    this.form = this.fb.group({});
    this.distributionRows = fb.array([]);
  }

  ngOnInit(): void {
    // Initialize form if none was passed as input
    this.form = this.form || this.fb.group({});

    this.form.addControl('distributionRows', this.distributionRows);
    this.form.markAsPristine();
  }

  @Input() set distributionList(distributionList: DistributionEntry[]) {
    if (distributionList) {
      FormUtil.clearArray(this.distributionRows);

      distributionList
        .map(d => this.createDistribution(d))
        .forEach(row => this.distributionRows.push(row));

      this.form.markAsDirty();
      if (this.readonly) {
        this.distributionRows.disable();
      }
    }
  }

  add(): void {
    this.distributionRows.insert(0, this.createDistribution(new DistributionEntry(undefined, undefined, DistributionType.EMAIL), true));
  }

  edit(control: UntypedFormControl): void {
    control.patchValue({edit: true});
  }

  save(control: UntypedFormControl): void {
    control.patchValue({edit: false});
    this.emitDistributionChange();
  }

  saveAll(): void {
    if (this.form.dirty) {
      this.distributionRows.controls.forEach(control => control.patchValue({edit: false}, {emitEvent: false}));
      this.emitDistributionChange();
    }
  }

  remove(index: number): void {
    this.distributionRows.removeAt(index);
    this.emitDistributionChange();
  }

  get statusChanges() {
    return this.form.statusChanges;
  }

  private createDistribution(distributionEntry: DistributionEntry, edit: boolean = false): UntypedFormGroup {
    return this.fb.group({
      id: [distributionEntry.id],
      name: [distributionEntry.name, Validators.required],
      type: [distributionEntry.uiType, Validators.required],
      email: [distributionEntry.email, Validators.email],
      streetAddress: [distributionEntry.postalAddress.streetAddress, Validators.minLength(1)],
      postalCode: [distributionEntry.postalAddress.postalCode, postalCodeValidator],
      city: [distributionEntry.postalAddress.city],
      edit: [edit]
    });
  }

  private toEntry(entryForm: DistributionEntryForm): DistributionEntry {
    const address = new PostalAddress(entryForm.streetAddress, entryForm.postalCode, entryForm.city);
    return new DistributionEntry(entryForm.id, entryForm.name, DistributionType[entryForm.type], entryForm.email, address);
  }

  private emitDistributionChange(): void {
    const current = this.distributionRows.getRawValue().map(row => this.toEntry(row));
    this.distributionChange.emit(current);
  }
}
