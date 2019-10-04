import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';
import {DistributionType} from '@model/common/distribution-type';
import {EnumUtil} from '@util/enum.util';
import {DistributionEntry} from '@model/common/distribution-entry';
import {postalCodeValidator} from '@util/complex-validator';
import {DistributionListEvents} from './distribution-list-events';
import isEqual from 'lodash/isEqual';
import {PostalAddress} from '@model/common/postal-address';

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
export class DistributionListComponent implements OnInit, OnDestroy {
  @Input() form: FormGroup;
  @Input() readonly: boolean;
  @Input() distributionList: Array<DistributionEntry> = [];

  @Output() distributionChange: EventEmitter<DistributionEntry[]> = new EventEmitter<DistributionEntry[]>();

  distributionRows: FormArray;
  distributionTypes = EnumUtil.enumValues(DistributionType);

  private addContactSubscription: Subscription;

  constructor(private fb: FormBuilder,
              private distributionListEvents: DistributionListEvents) {
    this.distributionRows = fb.array([]);
  }

  ngOnInit(): void {
    this.form.addControl('distributionRows', this.distributionRows);
    this.distributionList
      .map(d => this.createDistribution(d))
      .forEach(row => this.distributionRows.push(row));

    if (this.readonly) {
      this.distributionRows.disable();
    }

    this.addContactSubscription = this.distributionListEvents.distributionList$
      .subscribe(entry => this.addContact(entry));
  }

  ngOnDestroy(): void {
    this.addContactSubscription.unsubscribe();
  }

  add(): void {
    this.distributionRows.insert(0, this.createDistribution(new DistributionEntry(undefined, undefined, DistributionType.EMAIL), true));
  }

  edit(control: FormControl): void {
    control.patchValue({edit: true});
  }

  save(control: FormControl): void {
    control.patchValue({edit: false});
    this.emitDistributionChange();
  }

  remove(index: number): void {
    this.distributionRows.removeAt(index);
    this.emitDistributionChange();
  }

  private addContact(entry: DistributionEntry) {
    if (this.distributionRows.controls.filter(
        control => isEqual(control.value.email, entry.email)).length === 0) {
      this.distributionRows.insert(0, this.createDistribution(entry));
    }
  }

  private createDistribution(distributionEntry: DistributionEntry, edit: boolean = false): FormGroup {
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
