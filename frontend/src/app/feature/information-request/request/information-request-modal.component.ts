import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogConfig, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup} from '@angular/forms';
import {InformationRequest} from '@model/information-request/information-request';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {EnumUtil} from '@util/enum.util';
import {InformationRequestForm} from './information-request.form';
import {InformationRequestStatus} from '@model/information-request/information-request-status';

export const INFORMATION_REQUEST_MODAL_CONFIG: MatDialogConfig<InformationRequestData> = {
  width: '60vw'
};

export interface InformationRequestData {
  request?: InformationRequest;
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
    if (InformationRequestStatus.DRAFT !== this.data.request.status) {
      this.form.disable();
    }
  }

  saveDraft(): void {
    this.close(InformationRequestStatus.DRAFT);
  }

  onSubmit(): void {
    this.close(InformationRequestStatus.OPEN);
  }

  private close(status?: InformationRequestStatus): void {
    const originalRequest = this.request || new InformationRequest();
    const request = InformationRequestForm.toInformationRequest(this.form.value, originalRequest);
    request.status = status || request.status;
    this.dialogRef.close(request);
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
