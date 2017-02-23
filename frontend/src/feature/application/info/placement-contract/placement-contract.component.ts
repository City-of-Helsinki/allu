import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicantForm} from '../applicant/applicant.form';
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

  constructor(private fb: FormBuilder,
              route: ActivatedRoute,
              applicationState: ApplicationState) {
    super(route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
    let contract = <PlacementContract>this.application.extension || new PlacementContract();
    this.applicationForm.patchValue(PlacementContractForm.from(this.application, contract));
  }

  protected update(form: PlacementContractForm): Application {
    let application = this.application;
    application.name = 'Sijoitussopimus'; // Placement contracts have no name so set default
    application.uiStartTime = form.validityTimes.startTime;
    application.uiEndTime = form.validityTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.contactList = form.contacts;
    application.extension = PlacementContractForm.to(form, application.extension.specifiers);
    return application;
  }

  protected initForm() {
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: ['', Validators.required],
        endTime: ['', Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      diaryNumber: [''],
      additionalInfo: [''],
      generalTerms: ['']
    });
  }
}
