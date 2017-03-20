import {Component, OnInit, Input, OnDestroy} from '@angular/core';
import {FormGroup, FormBuilder, Validators, FormArray, FormControl} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';
import {EnumUtil} from '../../../util/enum.util';
import {DistributionEntry} from '../../../model/common/distribution-entry';
import {DistributionType} from '../../../model/common/distribution-type';
import {emailValidator, postalCodeValidator} from '../../../util/complex-validator';
import {ApplicationState} from '../../../service/application/application-state';
import {Some} from '../../../util/option';

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

  distributionRows: FormArray;
  distributionTypes = EnumUtil.enumValues(DistributionType);

  constructor(private fb: FormBuilder,
              private applicationState: ApplicationState) {
    this.distributionRows = fb.array([]);
  }

  ngOnInit(): void {
    this.form.addControl('distributionRows', this.distributionRows);

    this.applicationState.application.decisionDistributionList
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
