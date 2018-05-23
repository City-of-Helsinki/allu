import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';
import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicationStore} from '../../../../service/application/application-store';
import {TrafficArrangement} from '../../../../model/application/traffic-arrangement/traffic-arrangement';
import {TrafficArrangementForm} from './traffic-arrangement.form';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {TimeUtil} from '../../../../util/time.util';
import {ProjectService} from '../../../../service/project/project.service';
import {NotificationService} from '../../../../service/notification/notification.service';


@Component({
  selector: 'traffic-arrangement',
  viewProviders: [],
  templateUrl: './traffic-arrangement.component.html',
  styleUrls: []
})
export class TrafficArrangementComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(
    fb: FormBuilder,
    route: ActivatedRoute,
    applicationStore: ApplicationStore,
    notification: NotificationService,
    router: Router,
    projectService: ProjectService) {
    super(fb, route, applicationStore, notification, router, projectService);
  }

  ngOnInit(): any {
    super.ngOnInit();
  }

  protected initForm() {
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      pksCard: [false],
      calculatedPrice: [0],
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required],
      workPurpose: ['']
    });
  }

  protected update(form: TrafficArrangementForm): Application {
    const application = super.update(form);
    application.name = 'Liikennejärjestely'; // Traffic arrangements have no name so set default
    application.startTime = TimeUtil.toStartDate(form.validityTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.validityTimes.endTime);
    application.extension = TrafficArrangementForm.to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);

    const arrangement = <TrafficArrangement>application.extension || new TrafficArrangement();
    this.applicationForm.patchValue(TrafficArrangementForm.from(application, arrangement));
  }
}
