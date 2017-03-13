import {Component, OnInit, Input} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {Application} from '../../../model/application/application';
import {CommunicationType} from '../../../model/application/communication-type';
import {PublicityType} from '../../../model/application/publicity-type';
import {EnumUtil} from '../../../util/enum.util';

@Component({
  selector: 'communication',
  template: require('./communication.component.html'),
  styles: []
})
export class CommunicationComponent implements OnInit {

  @Input() form: FormGroup;
  @Input() application: Application;
  @Input() readonly: boolean;

  communicationForm: FormGroup;
  publicityTypes = EnumUtil.enumValues(PublicityType);

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.communicationForm = this.fb.group({
      communicationType: [this.application.communicationType || CommunicationType[CommunicationType.EMAIL], Validators.required],
      publicityType: [this.application.publicityType || PublicityType[PublicityType.PUBLIC], Validators.required]
    });
    this.form.addControl('communication', this.communicationForm);

    if (this.readonly) {
      this.communicationForm.disable();
    }
  }
}
