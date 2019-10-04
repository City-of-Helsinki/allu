import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Application} from '../../../model/application/application';
import {PublicityType} from '../../../model/application/publicity-type';
import {EnumUtil} from '../../../util/enum.util';
import {DistributionType} from '../../../model/common/distribution-type';
import {DistributionEntry} from '../../../model/common/distribution-entry';
import {Subscription} from 'rxjs';

@Component({
  selector: 'distribution',
  templateUrl: './distribution.component.html',
  styleUrls: []
})
export class DistributionComponent implements OnInit, OnDestroy {

  @Input() form: FormGroup;
  @Input() application: Application;
  @Input() readonly: boolean;

  @Output() distributionChange: EventEmitter<DistributionEntry[]> = new EventEmitter<DistributionEntry[]>();

  communicationForm: FormGroup;
  publicityTypes = EnumUtil.enumValues(PublicityType);
  distributionList: Array<DistributionEntry>;

  private recipientSubscription: Subscription;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.communicationForm = this.fb.group({
      publicityType: [this.application.decisionPublicityType || PublicityType[PublicityType.PUBLIC], Validators.required]
    });
    this.form.addControl('communication', this.communicationForm);
    this.distributionList = this.application.decisionDistributionList;

    if (this.readonly) {
      this.communicationForm.disable();
    }
  }

  ngOnDestroy(): void {
    if (this.recipientSubscription) {
      this.recipientSubscription.unsubscribe();
    }
  }
}
