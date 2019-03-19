import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';

import {Application} from '@model/application/application';
import {createDraftStructure, createStructure, from, ShortTermRentalForm, to} from './short-term-rental.form';
import {ShortTermRental} from '@model/application/short-term-rental/short-term-rental';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {ApplicationKind} from '@model/application/type/application-kind';
import {TimeUtil} from '@util/time.util';
import {takeUntil} from 'rxjs/internal/operators';
import {findTranslation} from '@util/translations';

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
  billable = false;
  recurringAllowed = false;

  private commercialCtrl: FormControl;

  billableChange(billable: boolean): void {
    this.billable = billable;
  }

  protected initForm() {
    super.initForm();

    this.commercialCtrl = <FormControl>this.applicationForm.get('commercial');
    this.commercialCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(value => this.updateCommercialLabel(value));
  }

  protected createExtensionForm(): FormGroup {
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
    this.updateNotBillable(application);
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

    return application;
  }

  private updateCommercialLabel(commercial: boolean) {
    this.commercialLabel = commercial ? COMMERCIAL : NON_COMMERCIAL;
  }

  private updateNotBillable(application: Application): void {
    const kind = application.kind;

    if (kindsWithNotBillableReason.indexOf(kind) >= 0) {
      application.notBillable = !this.billable;
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
}
