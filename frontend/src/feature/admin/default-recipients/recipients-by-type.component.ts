import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {DefaultRecipient} from '../../../model/common/default-recipient';
import {emailValidator} from '../../../util/complex-validator';
import {ApplicationType} from '../../../model/application/type/application-type';
import {EnumUtil} from '../../../util/enum.util';
import {DefaultRecipientHub} from '../../../service/recipients/default-recipient-hub';
import {NotificationService} from '../../../service/notification/notification.service';

@Component({
  selector: 'recipients-by-type',
  template: require('./recipients-by-type.component.html'),
  styles: []
})
export class RecipientsByTypeComponent implements OnInit {
  @Input() type: string;
  @Output() onItemCountChanged = new EventEmitter<number>();

  applicationTypes = EnumUtil.enumValues(ApplicationType);

  form: FormGroup;
  recipientRows: FormArray;

  constructor(private fb: FormBuilder, private recipientHub: DefaultRecipientHub) {
    this.recipientRows = fb.array([]);
    this.form = fb.group({
      recipientRows: this.recipientRows
    });
  }

  ngOnInit(): void {
    this.onItemCountChanged.emit(this.recipientRows.length);
    this.recipientHub.defaultRecipientsByType(this.type)
      .subscribe(recipients => this.replaceRecipients(recipients));
  }

  add(): void {
    this.recipientRows.push(this.createRecipient());
    this.onItemCountChanged.emit(this.recipientRows.length);
  }

  edit(control: FormControl): void {
    control.enable();
  }

  save(control: FormControl): void {
    const value = control.value;
    const recipient = new DefaultRecipient(value.id, value.email, value.applicationType);
    this.recipientHub.saveDefaultRecipient(recipient)
      .subscribe(
        saved => { control.disable(); },
        error => NotificationService.error(error));
  }

  remove(index: number): void {
    const recipient = this.recipientRows.at(index).value;
    this.recipientHub.removeDefaultRecipient(recipient.id)
      .subscribe(
        status => {},
        error => NotificationService.error(error));
    this.onItemCountChanged.emit(this.recipientRows.length);
  }

  private replaceRecipients(newRecipients: Array<DefaultRecipient>) {
    while (this.recipientRows.length > 0) {
      this.recipientRows.removeAt(0);
    }
    newRecipients.map(r => this.createRecipient(r)).forEach(r => this.recipientRows.push(r));
    this.recipientRows.disable();
  }

  private createRecipient(recipient: DefaultRecipient = DefaultRecipient.ofType(this.type)): FormGroup {
    return this.fb.group({
      id: [recipient.id],
      email: [recipient.email, [Validators.required, emailValidator]],
      applicationType: [recipient.applicationType, Validators.required]
    });
  }
}
