import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Application} from '@model/application/application';
import {PublicityType} from '@model/application/publicity-type';
import {EnumUtil} from '@util/enum.util';
import {DistributionEntry} from '@model/common/distribution-entry';
import {Subscription} from 'rxjs';
import {distributionChangeAllowed} from '@model/application/application-status';

@Component({
  selector: 'distribution',
  templateUrl: './distribution.component.html',
  styleUrls: []
})
export class DistributionComponent implements OnInit, OnDestroy {

  @Input() form: FormGroup;
  @Input() application: Application;
  @Input() distributionList: DistributionEntry[];
  @Input() readonly: boolean;

  @Output() distributionChange: EventEmitter<DistributionEntry[]> = new EventEmitter<DistributionEntry[]>();

  communicationForm: FormGroup;
  publicityTypes = EnumUtil.enumValues(PublicityType);
  distributionChangeAllowed = false;

  private recipientSubscription: Subscription;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.communicationForm = this.fb.group({
      publicityType: [this.application.decisionPublicityType || PublicityType[PublicityType.PUBLIC], Validators.required]
    });
    this.form.addControl('communication', this.communicationForm);

    if (this.readonly) {
      this.communicationForm.disable();
    }

    this.distributionChangeAllowed = distributionChangeAllowed(this.application.status);
  }

  ngOnDestroy(): void {
    if (this.recipientSubscription) {
      this.recipientSubscription.unsubscribe();
    }
  }
}
