import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {DefaultRecipient} from '../../../model/common/default-recipient';
import {ApplicationType} from '../../../model/application/type/application-type';
import {EnumUtil} from '../../../util/enum.util';
import {DefaultRecipientHub} from '../../../service/recipients/default-recipient-hub';
import {NotificationService} from '../../notification/notification.service';

@Component({
  selector: 'recipients-by-type',
  templateUrl: './recipients-by-type.component.html',
  styleUrls: []
})
export class RecipientsByTypeComponent implements OnInit {
  @Input() type: string;
  @Output() onItemCountChanged = new EventEmitter<number>();

  applicationTypes = Object.keys(ApplicationType);

  form: UntypedFormGroup;
  recipientRows: UntypedFormArray;

  constructor(private fb: UntypedFormBuilder,
              private recipientHub: DefaultRecipientHub,
              private notification: NotificationService) {
    this.recipientRows = fb.array([]);
    this.form = fb.group({
      recipientRows: this.recipientRows
    });
  }

  ngOnInit(): void {
    this.onItemCountChanged.emit(this.recipientRows.length);
    this.recipientHub.defaultRecipientsByApplicationType(this.type)
      .subscribe(recipients => this.replaceRecipients(recipients));
  }

  add(): void {
    this.recipientRows.push(this.createRecipient());
    this.onItemCountChanged.emit(this.recipientRows.length);
  }

  edit(control: UntypedFormControl): void {
    control.enable();
  }

  save(control: UntypedFormControl): void {
    const value = control.value;
    const recipient = new DefaultRecipient(value.id, value.email, value.applicationType);
    this.recipientHub.saveDefaultRecipient(recipient)
      .subscribe(
        saved => { control.disable(); },
        error => this.notification.errorInfo(error));
  }

  remove(index: number): void {
    const recipient = this.recipientRows.at(index).value;
    this.recipientHub.removeDefaultRecipient(recipient.id)
      .subscribe(
        status => {},
        error => this.notification.errorInfo(error));
    this.onItemCountChanged.emit(this.recipientRows.length);
  }

  private replaceRecipients(newRecipients: Array<DefaultRecipient>) {
    while (this.recipientRows.length > 0) {
      this.recipientRows.removeAt(0);
    }
    newRecipients.map(r => this.createRecipient(r)).forEach(r => this.recipientRows.push(r));
    this.recipientRows.disable();
  }

  private createRecipient(recipient: DefaultRecipient = DefaultRecipient.ofType(this.type)): UntypedFormGroup {
    return this.fb.group({
      id: [recipient.id],
      email: [recipient.email, [Validators.required, Validators.email]],
      applicationType: [recipient.applicationType, Validators.required]
    });
  }
}
