import {Component, Input, OnInit, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, FormControl, Validators, FormArray} from '@angular/forms';

import {StructureMeta} from '../../../model/application/structure-meta';
import {outdoorEventConfig} from '../outdoor-event/outdoor-event-config';
import {ApplicationHub} from '../../../service/application/application-hub';
import {Contact} from '../../../model/application/contact';
import {Application} from '../../../model/application/application';
import {translations} from '../../../util/translations';

@Component({
  selector: 'contact',
  viewProviders: [],
  template: require('./contact.component.html'),
  styles: []
})
export class ContactComponent implements OnInit, AfterViewInit {
  @Input() applicationForm: FormGroup;
  @Input() readonly: boolean;

  contactsForm: FormGroup;
  contacts: FormArray;
  meta: StructureMeta;
  translations = translations;

  constructor(private applicationHub: ApplicationHub, private route: ActivatedRoute, private fb: FormBuilder) {
    applicationHub.metaData().subscribe(meta => this.metadataLoaded(meta));
  }

  ngOnInit(): void {
    this.contacts = this.fb.array([]);
    this.contactsForm = this.fb.group({
      contacts: this.contacts
    });
    this.applicationForm.addControl('contacts', this.contacts);

    this.route.parent.data.subscribe((data: {application: Application}) => {
      data.application.contactList.forEach(contact => this.addContact(contact));
    });
  }

  ngAfterViewInit(): void {
    setTimeout(() => Materialize.updateTextFields(), 10);
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.meta = metadata;
  }

  private addContact(contact: Contact): void {
    this.contacts.push(this.createContact(contact));
  }

  private removeContact(index: number): void {
    this.contacts.removeAt(index);
  }

  private createContact(contact: Contact): FormGroup {
    return this.fb.group({
      id: contact.id,
      organizationId: contact.organizationId,
      name: [contact.name, [Validators.required, Validators.minLength(2)]],
      streetAddress: [contact.streetAddress],
      postalCode: [contact.postalCode],
      city: [contact.city],
      email: [contact.email, Validators.pattern('.+@.+\\..+')],
      phone: [contact.phone, Validators.minLength(2)]
    });
  }
}
