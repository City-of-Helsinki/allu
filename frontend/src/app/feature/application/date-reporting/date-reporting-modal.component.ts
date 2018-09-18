import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {DateReport} from '@feature/application/date-reporting/date-report';

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
  reportedDates: ReportedDateType[];
}

export const DATE_REPORTING_MODAL_CONFIG = {width: '600px'};

const defaultReportedDates = [ReportedDateType.WINTER_TIME_OPERATION, ReportedDateType.WORK_FINISHED];

@Component({
  selector: 'date-reporting-modal',
  templateUrl: './date-reporting-modal.component.html',
  styleUrls: []
})
export class DateReportingModalComponent implements OnInit {
  form: FormGroup;
  reportedDates: FormArray;


  constructor(public dialogRef: MatDialogRef<DateReportingModalComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DateReportingModalData,
              private fb: FormBuilder) {
  }

  ngOnInit(): void {
    const reportedDateTypes = this.data.reportedDates || defaultReportedDates;
    const groups = reportedDateTypes.map(type => this.createReportGroup(type));

    this.reportedDates = this.fb.array(groups);
    this.form = this.fb.group({
      reportedDates: this.reportedDates
    });
  }

  toggleReported(index: number): void {
    const ctrl = this.reportedDates.at(index);
    if (ctrl.disabled) {
      ctrl.enable();
    } else {
      ctrl.disable();
    }
  }

  save(): void {
    this.dialogRef.close(this.formToResult());
  }

  cancel(): void {
    this.dialogRef.close();
  }

  private createReportGroup(name: string): FormGroup {
    const group =  this.fb.group({
      name: name,
      reportingDate: [undefined, Validators.required],
      reportedDate: [undefined, Validators.required]
    });
    group.disable();
    return group;
  }

  private formToResult(): DateReport {
    return this.reportedDates.controls
      .filter(ctrl => ctrl.enabled)
      .map(ctrl => ctrl.value)
      .reduce((result, cur) => {
        result[cur.name] = {
          reportingDate: cur.reportingDate,
          reportedDate: cur.reportedDate
        };
        return result;
      }, {});
  }
}
