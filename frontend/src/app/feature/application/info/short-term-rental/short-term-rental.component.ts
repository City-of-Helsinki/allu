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

    if (application.kinds.some(kind => ApplicationKind.PROMOTION_OR_SALES === kind)) {
      application.notBillable = !this.billable;
      if (!this.billable && !application.notBillableReason) {
        application.notBillableReason = findTranslation('application.shortTermRental.notBillableReason');
      }
    }
  }

  protected update(form: ShortTermRentalForm): Application {
    const application = super.update(form);
    application.startTime = TimeUtil.toStartDate(form.rentalTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.rentalTimes.endTime);
    application.extension = to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  private updateCommercialLabel(commercial: boolean) {
    this.commercialLabel = commercial ? COMMERCIAL : NON_COMMERCIAL;
  }
}
