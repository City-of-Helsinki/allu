import {Component, OnInit} from '@angular/core';
import {Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {PlacementContract} from '../../../../model/application/placement-contract/placement-contract';
import {PlacementContractForm} from './placement-contract.form';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {TimeUtil} from '../../../../util/time.util';


@Component({
  selector: 'placement-contract',
  viewProviders: [],
  templateUrl: './placement-contract.component.html',
  styleUrls: []
})
export class PlacementContractComponent extends ApplicationInfoBaseComponent implements OnInit {

  protected initForm() {
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      terminationDate: [undefined],
      propertyIdentificationNumber: [''],
      calculatedPrice: [0],
      additionalInfo: [''],
      contractText: ['']
    });
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);
    const contract = <PlacementContract>application.extension || new PlacementContract();
    this.applicationForm.patchValue(PlacementContractForm.from(application, contract));
  }

  protected update(form: PlacementContractForm): Application {
    const application = super.update(form);
    application.name = 'Sijoitussopimus'; // Placement contracts have no name so set default
    application.startTime = TimeUtil.toStartDate(form.validityTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.validityTimes.endTime);
    application.extension = PlacementContractForm.to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }
}
