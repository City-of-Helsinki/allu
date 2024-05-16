import {Component, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';

import {ComplexValidator} from '@util/complex-validator';
import {from, NoteForm, to} from './note.form';
import {ApplicationInfoBaseComponent} from '@feature/application/info/application-info-base.component';
import {MAX_YEAR, MIN_YEAR, TimeUtil} from '@util/time.util';
import {Application} from '@model/application/application';

@Component({
  selector: 'note',
  viewProviders: [],
  templateUrl: './note.component.html',
  styleUrls: []
})
export class NoteComponent extends ApplicationInfoBaseComponent implements OnInit, OnDestroy {

  private validityTimesControl: UntypedFormControl;
  private recurringEndYearSubscription: Subscription;

  ngOnDestroy(): void {
    this.recurringEndYearSubscription.unsubscribe();
  }

  protected initForm() {
    super.initForm();

    this.validityTimesControl = <UntypedFormControl>this.applicationForm.controls['validityTimes'];
    this.recurringEndYearSubscription = this.applicationForm.controls['recurringEndYear'].valueChanges
        .subscribe(val => this.onRecurringEndYearChanged(val));
  }

  protected createExtensionForm(): UntypedFormGroup {
    return this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      description: [''],
      recurringEndYear: [undefined, ComplexValidator.betweenOrEmpty(MIN_YEAR, MAX_YEAR)]
    });
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);
    this.applicationForm.patchValue(from(application));
  }

  protected update(form: NoteForm) {
    const application = super.update(form);
    application.startTime = TimeUtil.toStartDate(form.validityTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.validityTimes.endTime);
    application.recurringEndTime = TimeUtil.dateWithYear(application.endTime, form.recurringEndYear);
    application.extension = to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  private onRecurringEndYearChanged(val: number) {
    if (val) {
      this.validityTimesControl.setValidators([
        ComplexValidator.durationAtMax('startTime', 'endTime', 1, 'year'),
        ComplexValidator.startBeforeEnd('startTime', 'endTime')
      ]);
    } else {
      this.validityTimesControl.setValidators(ComplexValidator.startBeforeEnd('startTime', 'endTime'));
    }
    this.validityTimesControl.updateValueAndValidity();
  }
}
