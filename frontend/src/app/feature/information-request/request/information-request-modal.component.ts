import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup} from '@angular/forms';
import {InformationRequest} from '@model/information-request/information-request';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {EnumUtil} from '../../../util/enum.util';
import {InformationRequestForm} from './information-request.form';

export interface InformationRequestData {
  request?: InformationRequest;
}

export interface InformationRequestInfo {
  draft: boolean;
  request: InformationRequest;
}

@Component({
  selector: 'app-information-request-modal',
  templateUrl: './information-request-modal.component.html',
  styleUrls: ['./information-request-modal.component.scss']
})
export class InformationRequestModalComponent implements OnInit {

  fieldKeys = EnumUtil.enumValues(InformationRequestFieldKey);
  form: FormGroup;
  request: InformationRequest;

  constructor(private dialogRef: MatDialogRef<InformationRequestModalComponent>,
              private fb: FormBuilder,
              @Inject(MAT_DIALOG_DATA) public data: InformationRequestData) {
    this.form = InformationRequestForm.formGroup(fb, data.request);
    this.request = data.request;
  }

  ngOnInit() {
  }

  saveDraft(): void {
    this.close(true);
  }

  onSubmit(): void {
    this.close();
  }

  private close(draft: boolean = false): void {
    const originalRequest = this.request || new InformationRequest();
    const request = InformationRequestForm.toInformationRequest(this.form.value, originalRequest);
    this.dialogRef.close({ draft, request });
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
