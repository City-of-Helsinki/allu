import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogConfig, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup} from '@angular/forms';
import {InformationRequestService} from '@service/application/information-request.service';
import {InformationRequest} from '@model/information-request/information-request';
import {Application} from '@model/application/application';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {EnumUtil} from '../../../util/enum.util';
import {NumberUtil} from '@util/number.util';
import {InformationRequestForm} from './information-request.form';

export interface InformationRequestData {
  application?: Application;
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
  application: Application;
  request: InformationRequest;

  constructor(private dialogRef: MatDialogRef<InformationRequestModalComponent>,
              private fb: FormBuilder,
              private service: InformationRequestService,
              @Inject(MAT_DIALOG_DATA) public data: InformationRequestData) {
    this.form = InformationRequestForm.formGroup(fb, data.request);
    this.application = data.application;
    this.request = data.request;
  }

  ngOnInit() {
  }

  onSubmit(): void {
    if (!this.request) {
      this.request = new InformationRequest;
      this.request.applicationId = this.application.id;
    }
    this.request = InformationRequestForm.toInformationRequest(this.form.value, this.request);
    if (NumberUtil.isDefined(this.request.informationRequestId)) {
      this.service.update(this.request).subscribe(result => this.dialogRef.close(result));
    } else {
      this.service.create(this.request).subscribe(result => this.dialogRef.close(result));
    }
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
