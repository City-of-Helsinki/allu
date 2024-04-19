import {Component, Inject, OnInit} from '@angular/core';
import {MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA, MatLegacyDialogConfig as MatDialogConfig, MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {InformationRequest} from '@model/information-request/information-request';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {EnumUtil} from '@util/enum.util';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {toFormGroup, toInformationRequestFields} from '@feature/information-request/request/information-request.form';

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
  form: UntypedFormGroup;
  request: InformationRequest;

  constructor(private dialogRef: MatDialogRef<InformationRequestModalComponent>,
              private fb: UntypedFormBuilder,
              @Inject(MAT_DIALOG_DATA) public data: InformationRequestData) {
    this.request = data.request || new InformationRequest();
    this.form = toFormGroup(fb, this.fieldKeys, this.request);
  }

  ngOnInit() {
    if (InformationRequestStatus.DRAFT !== this.request.status) {
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
    this.dialogRef.close({
      ...this.request,
      fields: toInformationRequestFields(this.form.value),
      status: status ? status : this.request.status
    });
  }

  cancel(): void {
    this.dialogRef.close();
  }

  onSelectedChange(field: string, selected: boolean) {
    const control = this.form.get(field);
    if (selected) {
      control.setValidators(Validators.required);
    } else {
      control.clearValidators();
    }
    this.form.get(field).updateValueAndValidity();
  }
}
