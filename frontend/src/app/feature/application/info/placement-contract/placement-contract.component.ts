import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicationStore} from '../../../../service/application/application-store';
import {PlacementContract} from '../../../../model/application/placement-contract/placement-contract';
import {PlacementContractForm} from './placement-contract.form';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {ProjectHub} from '../../../../service/project/project-hub';


@Component({
  selector: 'placement-contract',
  viewProviders: [],
  templateUrl: './placement-contract.component.html',
  styleUrls: []
})
export class PlacementContractComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(
    fb: FormBuilder,
    route: ActivatedRoute,
    applicationStore: ApplicationStore,
    router: Router,
    projectHub: ProjectHub) {
    super(fb, route, applicationStore, router, projectHub);
  }

  ngOnInit(): any {
    super.ngOnInit();
  }

  protected initForm() {
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      diaryNumber: [''],
      calculatedPrice: [0],
      additionalInfo: [''],
      generalTerms: ['']
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
    application.startTime = form.validityTimes.startTime;
    application.endTime = form.validityTimes.endTime;
    application.extension = PlacementContractForm.to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }
}
