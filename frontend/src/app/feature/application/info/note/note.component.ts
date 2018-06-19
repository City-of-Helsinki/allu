import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';

import {ComplexValidator} from '../../../../util/complex-validator';
import {NoteForm} from './note.form';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {MAX_YEAR, MIN_YEAR, TimeUtil} from '../../../../util/time.util';
import {Application} from '../../../../model/application/application';

@Component({
  selector: 'note',
  viewProviders: [],
  templateUrl: './note.component.html',
  styleUrls: []
})
export class NoteComponent extends ApplicationInfoBaseComponent implements OnInit, OnDestroy {

  private validityTimesControl: FormControl;
  private recurringEndYearSubscription: Subscription;

  ngOnDestroy(): void {
    this.recurringEndYearSubscription.unsubscribe();
  }

  protected initForm() {
    this.applicationForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      description: [''],
      recurringEndYear: [undefined, ComplexValidator.betweenOrEmpty(MIN_YEAR, MAX_YEAR)]
    });
    this.validityTimesControl = <FormControl>this.applicationForm.controls['validityTimes'];
    this.recurringEndYearSubscription = this.applicationForm.controls['recurringEndYear'].valueChanges
        .subscribe(val => this.onRecurringEndYearChanged(val));
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);
    this.applicationForm.patchValue(NoteForm.from(application));
  }

  protected update(form: NoteForm) {
    const application = super.update(form);
    application.name = form.name;
    application.startTime = TimeUtil.toStartDate(form.validityTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.validityTimes.endTime);
    application.recurringEndTime = TimeUtil.dateWithYear(application.endTime, form.recurringEndYear);
    application.extension = NoteForm.to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  private onRecurringEndYearChanged(val: number) {
    if (val) {
      this.validityTimesControl.setValidators(
          [ComplexValidator.durationAtMax('startTime', 'endTime', 364),
           ComplexValidator.startBeforeEnd('startTime', 'endTime')]);
    } else {
      this.validityTimesControl.setValidators(ComplexValidator.startBeforeEnd('startTime', 'endTime'));
    }
    this.validityTimesControl.updateValueAndValidity();
  }
}
