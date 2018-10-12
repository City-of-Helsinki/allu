import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {ApplicationDateReport} from '@model/application/application-date-report';

export enum ReporterType {
  CUSTOMER = 'customer',
  OFFICIAL = 'official'
}

export enum ReportedDateType {
  WINTER_TIME_OPERATION = 'winterTimeOperation',
  WORK_FINISHED = 'workFinished',
}

export interface DateReportingModalData {
  reporterType: ReporterType;
  dateType: ReportedDateType;
  date?: Date;
  reported?: Date;
}

export const DATE_REPORTING_MODAL_CONFIG = {width: '600px'};

@Component({
  selector: 'date-reporting-modal',
  templateUrl: './date-reporting-modal.component.html',
  styleUrls: []
})
export class DateReportingModalComponent implements OnInit {
  form: FormGroup;

  constructor(public dialogRef: MatDialogRef<DateReportingModalComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DateReportingModalData,
              private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      reportingDate: [this.data.date, Validators.required],
      reportedDate: [this.data.reported, Validators.required]
    });
  }

  save(): void {
    const formValues = this.form.value;
    this.dialogRef.close(new ApplicationDateReport(formValues.reportedDate, formValues.reportingDate));
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
