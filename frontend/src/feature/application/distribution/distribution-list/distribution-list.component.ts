import {Component, Input, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';

import {DistributionType} from '../../../../model/common/distribution-type';
import {EnumUtil} from '../../../../util/enum.util';
import {DistributionEntry} from '../../../../model/common/distribution-entry';
import {emailValidator, postalCodeValidator} from '../../../../util/complex-validator';

@Component({
  selector: 'distribution-list',
  template: require('./distribution-list.component.html'),
  styles: [
    require('./distribution-list.component.scss')
  ]
})
export class DistributionListComponent implements OnInit {
  @Input() form: FormGroup;
  @Input() readonly: boolean;
  @Input() distributionList: Array<DistributionEntry> = [];

  distributionRows: FormArray;
  distributionTypes = EnumUtil.enumValues(DistributionType);

  constructor(private fb: FormBuilder) {
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
  }

  add(): void {
    this.distributionRows.insert(0, this.createDistribution(new DistributionEntry(undefined, undefined, DistributionType.EMAIL), true));
  }

  edit(control: FormControl): void {
    control.patchValue({edit: true});
  }

  save(control: FormControl): void {
    control.patchValue({edit: false});
  }

  remove(index: number): void {
    this.distributionRows.removeAt(index);
  }

  private createDistribution(distributionEntry: DistributionEntry, edit: boolean = false): FormGroup {
    return this.fb.group({
      id: [distributionEntry.id],
      name: [distributionEntry.name, Validators.required],
      type: [distributionEntry.uiType, Validators.required],
      email: [distributionEntry.email, emailValidator],
      streetAddress: [distributionEntry.postalAddress.streetAddress, Validators.minLength(1)],
      postalCode: [distributionEntry.postalAddress.postalCode, postalCodeValidator],
      city: [distributionEntry.postalAddress.city],
      edit: [edit]
    });
  }
}
