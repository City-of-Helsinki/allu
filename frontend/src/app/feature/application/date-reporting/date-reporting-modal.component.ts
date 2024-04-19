import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Component, Inject, OnInit} from '@angular/core';
import {MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA, MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {DateReport} from '@model/application/date-report';
import {FormUtil} from '@util/form.util';

export enum ReporterType {
  CUSTOMER = 'customer',
  OFFICIAL = 'official'
}

export enum ReportedDateType {
  WINTER_TIME_OPERATION = 'winterTimeOperation',
  WORK_FINISHED = 'workFinished',
  VALIDITY = 'validity'
}

export interface DateReportingModalData {
  reporterType: ReporterType;
  dateType: ReportedDateType;
  reportedDate?: Date;
  reportedEndDate?: Date;
  reportingDate?: Date;
}

export const DATE_REPORTING_MODAL_CONFIG = {width: '600px'};

@Component({
  selector: 'date-reporting-modal',
  templateUrl: './date-reporting-modal.component.html',
  styleUrls: []
})
export class DateReportingModalComponent implements OnInit {
  form: UntypedFormGroup;
  reportedDateTranslationKey: string;
  reportedEndDateTranslationKey: string;

  constructor(public dialogRef: MatDialogRef<DateReportingModalComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DateReportingModalData,
              private fb: UntypedFormBuilder) {
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      reportedDate: [this.data.reportedDate, Validators.required],
      reportedEndDate: [this.data.reportedEndDate],
      reportingDate: [this.data.reportingDate, Validators.required]
    });

    this.initDateTranslations();
  }

  save(): void {
    const formValues = this.form.value;
    this.dialogRef.close(new DateReport(formValues.reportingDate, formValues.reportedDate, formValues.reportedEndDate));
  }

  cancel(): void {
    this.dialogRef.close();
  }

  get maxReportedDate() {
    return FormUtil.getValue(this.form, 'reportedEndDate');
  }

  get minReportedEndDate() {
    return FormUtil.getValue(this.form, 'reportedDate');
  }

  private initDateTranslations(): void {
    if (this.data.dateType === ReportedDateType.VALIDITY) {
      this.reportedDateTranslationKey = 'dateReporting.dateField.reportedStartDate';
      this.reportedEndDateTranslationKey = 'dateReporting.dateField.reportedEndDate';
    } else {
      this.reportedDateTranslationKey = 'dateReporting.dateField.reportedDate';
    }
  }
}
