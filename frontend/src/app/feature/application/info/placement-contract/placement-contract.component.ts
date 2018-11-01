import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';

import {Application} from '@model/application/application';
import {ComplexValidator} from '@util/complex-validator';
import {PlacementContract} from '@model/application/placement-contract/placement-contract';
import {from, PlacementContractForm, to} from './placement-contract.form';
import {ApplicationInfoBaseComponent} from '@feature/application/info/application-info-base.component';
import {TimeUtil} from '@util/time.util';


@Component({
  selector: 'placement-contract',
  viewProviders: [],
  templateUrl: './placement-contract.component.html',
  styleUrls: []
})
export class PlacementContractComponent extends ApplicationInfoBaseComponent implements OnInit {
  protected createExtensionForm(): FormGroup {
    return this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      terminationDate: [undefined],
      propertyIdentificationNumber: [''],
      calculatedPrice: [0],
      additionalInfo: [''],
      contractText: [''],
      terms: [''],
      rationale: ['']
    });
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);
    const contract = <PlacementContract>application.extension || new PlacementContract();
    this.applicationForm.patchValue(from(application, contract));
  }

  protected update(form: PlacementContractForm): Application {
    const application = super.update(form);
    application.startTime = TimeUtil.toStartDate(form.validityTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.validityTimes.endTime);
    application.extension = to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }
}
