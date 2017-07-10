import {Component, OnInit, Input, OnDestroy} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {Application} from '../../../model/application/application';
import {PublicityType} from '../../../model/application/publicity-type';
import {EnumUtil} from '../../../util/enum.util';
import {DistributionType} from '../../../model/common/distribution-type';
import {DefaultRecipientHub} from '../../../service/recipients/default-recipient-hub';
import {DistributionEntry} from '../../../model/common/distribution-entry';
import {Subscription} from 'rxjs/Subscription';
import {DefaultRecipient} from '../../../model/common/default-recipient';

@Component({
  selector: 'distribution',
  template: require('./distribution.component.html'),
  styles: []
})
export class DistributionComponent implements OnInit, OnDestroy {

  @Input() form: FormGroup;
  @Input() application: Application;
  @Input() readonly: boolean;

  communicationForm: FormGroup;
  publicityTypes = EnumUtil.enumValues(PublicityType);
  distributionList: Array<DistributionEntry> = undefined;

  private recipientSubscription: Subscription;

  constructor(private fb: FormBuilder, private defaultRecipientHub: DefaultRecipientHub) {}

  ngOnInit(): void {
    this.communicationForm = this.fb.group({
      distributionType: [this.application.decisionDistributionType || DistributionType[DistributionType.EMAIL], Validators.required],
      publicityType: [this.application.decisionPublicityType || PublicityType[PublicityType.PUBLIC], Validators.required]
    });
    this.form.addControl('communication', this.communicationForm);
    this.initDistributionList();

    if (this.readonly) {
      this.communicationForm.disable();
    }
  }

  ngOnDestroy(): void {
    if (this.recipientSubscription) {
      this.recipientSubscription.unsubscribe();
    }
  }

  initDistributionList(): void {
    // Only use default recipients when creating new application
    if (!this.readonly && this.application.id === undefined) {
      this.recipientSubscription = this.defaultRecipientHub.defaultRecipientsByApplicationType(this.application.type)
        .map(recipients => recipients.map(r => this.toDistributionEntry(r)))
        .subscribe(distributionEntries => this.distributionList = distributionEntries);
    } else {
      this.distributionList = this.application.decisionDistributionList;
    }
  }

  private toDistributionEntry(recipient: DefaultRecipient): DistributionEntry {
    const de = new DistributionEntry();
    de.name = recipient.email;
    de.email = recipient.email;
    de.distributionType = DistributionType.EMAIL;
    return de;
  }
}
