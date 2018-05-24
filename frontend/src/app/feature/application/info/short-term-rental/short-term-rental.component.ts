import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormControl} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {ShortTermRentalForm} from './short-term-rental.form';
import {ShortTermRental} from '../../../../model/application/short-term-rental/short-term-rental';
import {ApplicationStore} from '../../../../service/application/application-store';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {ApplicationKind} from '../../../../model/application/type/application-kind';
import {TimeUtil} from '../../../../util/time.util';
import {ProjectService} from '../../../../service/project/project.service';
import {NotificationService} from '../../../../service/notification/notification.service';
import {takeUntil} from 'rxjs/internal/operators';

const COMMERCIAL = 'application.shortTermRental.commercial';
const NON_COMMERCIAL = 'application.shortTermRental.nonCommercial';

@Component({
  selector: 'short-term-rental',
  viewProviders: [],
  templateUrl: './short-term-rental.component.html',
  styleUrls: []
})
export class ShortTermRentalComponent extends ApplicationInfoBaseComponent implements OnInit {

  showCommercial = false;
  commercialLabel: string;

  private commercialCtrl: FormControl;

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
    const draft = this.applicationStore.snapshot.draft;
    this.completeFormStructure = ShortTermRentalForm.createStructure(this.fb);
    this.draftFormStructure = ShortTermRentalForm.createDraftStructure(this.fb);

    this.applicationForm = draft
      ? this.fb.group(this.draftFormStructure)
      : this.fb.group(this.completeFormStructure);

    this.commercialCtrl = <FormControl>this.applicationForm.get('commercial');

    this.commercialCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(value => this.updateCommercialLabel(value));
  }

  protected onApplicationChange(application: Application): any {
    super.onApplicationChange(application);

    const rental = <ShortTermRental>application.extension || new ShortTermRental();
    const formValue = ShortTermRentalForm.from(application, rental);
    this.applicationForm.patchValue(formValue);
    this.showCommercial = application.kinds.some(kind => ApplicationKind.BRIDGE_BANNER === kind);
    this.updateCommercialLabel(rental.commercial);
  }

  protected update(form: ShortTermRentalForm): Application {
    const application = super.update(form);
    application.name = form.name;
    application.startTime = TimeUtil.toStartDate(form.rentalTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.rentalTimes.endTime);
    application.extension = ShortTermRentalForm.to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  private updateCommercialLabel(commercial: boolean) {
    this.commercialLabel = commercial ? COMMERCIAL : NON_COMMERCIAL;
  }
}
