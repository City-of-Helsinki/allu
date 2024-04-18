import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';

import {Application} from '@model/application/application';
import {createDraftStructure, createStructure, from, ShortTermRentalForm, to} from './short-term-rental.form';
import {ShortTermRental} from '@model/application/short-term-rental/short-term-rental';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {ApplicationKind} from '@model/application/type/application-kind';
import {TimeUtil} from '@util/time.util';
import {filter, map, switchMap, takeUntil} from 'rxjs/operators';
import {combineLatest} from 'rxjs';
import {findTranslation} from '@util/translations';
import {ActivatedRoute, Router} from '@angular/router';
import {ApplicationStore} from '@service/application/application-store';
import {ApplicationService} from '@service/application/application.service';
import {NotificationService} from '@feature/notification/notification.service';
import {ProjectService} from '@service/project/project.service';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {ConfigurationHelperService} from '@service/config/configuration-helper.service';
import {Observable} from 'rxjs/internal/Observable';
import {setValidatorsAndValidate} from '@feature/common/validation/validation-util';
import {TimePeriod} from '@feature/application/info/time-period';
import {ComplexValidator} from '@util/complex-validator';
import {DateFilter, defaultDateFilter} from '@util/date-filter';
import * as fromDecision from '@feature/decision/reducers';

const COMMERCIAL = 'application.shortTermRental.commercial';
const NON_COMMERCIAL = 'application.shortTermRental.nonCommercial';
const kindsWithNotBillableReason = [
  ApplicationKind.PROMOTION_OR_SALES,
  ApplicationKind.SUMMER_TERRACE,
  ApplicationKind.WINTER_TERRACE
];
const kindsWithRecurring = [
  ApplicationKind.SUMMER_TERRACE,
  ApplicationKind.WINTER_TERRACE,
  ApplicationKind.PARKLET
];

@Component({
  selector: 'short-term-rental',
  viewProviders: [],
  templateUrl: './short-term-rental.component.html',
  styleUrls: []
})
export class ShortTermRentalComponent extends ApplicationInfoBaseComponent implements OnInit {

  showCommercial = false;
  commercialLabel: string;
  recurringAllowed = false;
  dateFilter: DateFilter;
  kind$: Observable<ApplicationKind>;
  maxEndDate$: Observable<Date>;
  minStartDate$: Observable<Date>;
  timePeriod$: Observable<TimePeriod>;
  terminationDate$: Observable<Date>;

  private commercialCtrl: UntypedFormControl;

  constructor(fb: UntypedFormBuilder,
              route: ActivatedRoute,
              applicationStore: ApplicationStore,
              applicationService: ApplicationService,
              notification: NotificationService,
              router: Router,
              projectService: ProjectService,
              store: Store<fromRoot.State>,
              private configurationHelper: ConfigurationHelperService) {
    super(fb, route, applicationStore, applicationService, notification, router, projectService, store);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.terminationDate$ = this.store.pipe(
      select(fromDecision.getTermination),
      filter(termination => termination && !!termination.terminationDecisionTime),
      map(termination => termination.expirationTime)
    );
  }

  protected initForm() {
    super.initForm();

    this.commercialCtrl = <UntypedFormControl>this.applicationForm.get('commercial');
    this.commercialCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(value => this.updateCommercialLabel(value));

    this.kind$ = this.store.pipe(select(fromApplication.getKind));

    this.timePeriod$ = this.store.pipe(
      select(fromApplication.getKind),
      switchMap((kind: ApplicationKind) => this.configurationHelper.getTimePeriodForKind(kind)),
      takeUntil(this.destroy)
    );

    this.maxEndDate$ = combineLatest(
      this.applicationForm.get('rentalTimes.startTime').valueChanges,
      this.timePeriod$.pipe(map(period => period ? period.endTime : undefined))
    ).pipe(
      map(([startTime, endTime]) => TimeUtil.toTimePeriodEnd(startTime, endTime))
    );

    this.minStartDate$ = combineLatest(
      this.applicationForm.get('rentalTimes.endTime').valueChanges,
      this.timePeriod$.pipe(map(period => period ? period.startTime : undefined))
    ).pipe(
      map(([endTime, startTime]) => TimeUtil.toTimePeriodStart(endTime, startTime))
    );

    this.timePeriod$.subscribe(timePeriod => this.onTimePeriodChange(timePeriod));
  }

  protected createExtensionForm(): UntypedFormGroup {
    const draft = this.applicationStore.snapshot.draft;
    this.completeFormStructure = createStructure(this.fb);
    this.draftFormStructure = createDraftStructure(this.fb);

    return draft
      ? this.fb.group(this.draftFormStructure)
      : this.fb.group(this.completeFormStructure);
  }

  protected onApplicationChange(application: Application): any {
    super.onApplicationChange(application);

    const rental = <ShortTermRental>application.extension || new ShortTermRental();
    const formValue = from(application, rental);
    this.applicationForm.patchValue(formValue);
    this.showCommercial = application.kinds.some(kind => ApplicationKind.BRIDGE_BANNER === kind);
    this.updateCommercialLabel(rental.commercial);
    this.recurringAllowed = kindsWithRecurring.indexOf(application.kind) >= 0;
  }

  protected update(form: ShortTermRentalForm): Application {
    const application = super.update(form);
    application.startTime = TimeUtil.toStartDate(form.rentalTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.rentalTimes.endTime);
    application.recurringEndTime = TimeUtil.dateWithYear(application.endTime, form.recurringEndYear);
    application.extension = to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;
    this.updateNotBillable(application);

    return application;
  }

  private updateCommercialLabel(commercial: boolean) {
    this.commercialLabel = commercial ? COMMERCIAL : NON_COMMERCIAL;
  }

  private updateNotBillable(application: Application): void {
    const kind = application.kind;

    if (kindsWithNotBillableReason.indexOf(kind) >= 0) {
      application.notBillable = !(<ShortTermRental>application.extension).billableSalesArea;
      this.updateNotBillableFor(application, kind);
    }
  }

  private updateNotBillableFor(application: Application, kind: ApplicationKind): void {
    if (application.notBillable && !application.notBillableReason) {
      switch (kind) {
        case ApplicationKind.PROMOTION_OR_SALES:
          application.notBillableReason = findTranslation('application.shortTermRental.promotionOrSalesNotBillableReason');
          break;
        case ApplicationKind.SUMMER_TERRACE:
        case ApplicationKind.WINTER_TERRACE:
          application.notBillableReason = findTranslation('application.shortTermRental.terraceNotBillableReason');
      }
    }
  }

  private onTimePeriodChange(timePeriod: TimePeriod) {
    if (timePeriod) {
      const validators = [Validators.required, ComplexValidator.inTimePeriod(timePeriod.startTime, timePeriod.endTime)];
      setValidatorsAndValidate(this.applicationForm.get('rentalTimes.startTime'), validators);
      setValidatorsAndValidate(this.applicationForm.get('rentalTimes.endTime'), validators);
      this.dateFilter = (date: Date) => TimeUtil.isInTimePeriod(date, timePeriod.startTime, timePeriod.endTime);
    } else {
      setValidatorsAndValidate(this.applicationForm.get('rentalTimes.startTime'), [Validators.required]);
      setValidatorsAndValidate(this.applicationForm.get('rentalTimes.endTime'), [Validators.required]);
      this.dateFilter = defaultDateFilter;
    }
  }
}
