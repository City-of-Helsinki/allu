import {Component, OnInit, Input} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {Application} from '../../../model/application/application';
import {PublicityType} from '../../../model/application/publicity-type';
import {EnumUtil} from '../../../util/enum.util';
import {DistributionType} from '../../../model/common/distribution-type';

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
      distributionType: [this.application.decisionDistributionType || DistributionType[DistributionType.EMAIL], Validators.required],
      publicityType: [this.application.decisionPublicityType || PublicityType[PublicityType.PUBLIC], Validators.required]
    });
    this.form.addControl('communication', this.communicationForm);

    if (this.readonly) {
      this.communicationForm.disable();
    }
  }
}
