import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Contact} from '@model/customer/contact';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';

@Component({
  selector: 'contact-acceptance',
  templateUrl: './contact-acceptance.component.html'
})
export class ContactAcceptanceComponent implements OnInit {
  @Input() newContact: Contact;
  @Input() formArray: FormArray;

  @Output() contactChanges: EventEmitter<Contact> = new EventEmitter<Contact>();

  form: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({});
    this.formArray.push(this.form);
  }
}
