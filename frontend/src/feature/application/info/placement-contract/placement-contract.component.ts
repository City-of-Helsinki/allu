import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicationState} from '../../../../service/application/application-state';
import {PlacementContract} from '../../../../model/application/placement-contract/placement-contract';
import {PlacementContractForm} from './placement-contract.form';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';


@Component({
  selector: 'placement-contract',
  viewProviders: [],
  template: require('./placement-contract.component.html'),
  styles: []
})
export class PlacementContractComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(fb: FormBuilder, route: ActivatedRoute, applicationState: ApplicationState) {
    super(fb, route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
    let contract = <PlacementContract>this.application.extension || new PlacementContract();
    this.applicationForm.patchValue(PlacementContractForm.from(this.application, contract));
  }

  protected update(form: PlacementContractForm): Application {
    let application = super.update(form);
    application.name = 'Sijoitussopimus'; // Placement contracts have no name so set default
    application.startTime = form.validityTimes.startTime;
    application.endTime = form.validityTimes.endTime;
    application.extension = PlacementContractForm.to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  protected initForm() {
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      diaryNumber: [''],
      calculatedPrice: [0],
      priceOverride: [undefined, ComplexValidator.greaterThanOrEqual(0)],
      priceOverrideReason: [''],
      additionalInfo: [''],
      generalTerms: ['']
    });
  }
}
