import {Component, OnInit, Input} from '@angular/core';
import {MdDialogRef} from '@angular/material';
import {Observable} from 'rxjs/Observable';
import {FormGroup, FormBuilder, FormControl, Validators, FormArray} from '@angular/forms';

import {ApplicationHub} from '../../../service/application/application-hub';
import {DefaultText} from '../../../model/application/cable-report/default-text';
import {CableInfoType} from '../../../model/application/cable-report/cable-info-type';
import {translations} from '../../../util/translations';

@Component({
  selector: 'default-text-modal',
  template: require('./default-text-modal.component.html'),
  styles: []
})
export class DefaultTextModalComponent implements OnInit {
  @Input() type: CableInfoType;

  typeName: string;
  defaultTextform: FormGroup;
  defaultTexts: FormArray;
  changed = new Set<number>();
  translations = translations;

  constructor(public dialogRef: MdDialogRef<DefaultTextModalComponent>,
              private fb: FormBuilder,
              private applicationHub: ApplicationHub) {
    this.defaultTexts = fb.array([]);
    this.defaultTextform = fb.group({
      defaultTexts: this.defaultTexts
    });
  }

  ngOnInit(): void {
    this.applicationHub.loadDefaultTexts()
      .map(texts => texts.filter(text => text.type === this.type))
      .subscribe(texts => texts.forEach(text => this.addText(text)));

    this.defaultTextform.valueChanges.subscribe(form => console.log('changes', form));
    this.typeName = CableInfoType[this.type];
  }

  addText(defaultText: DefaultText) {
    let added = defaultText ? defaultText : new DefaultText();
    this.defaultTexts.push(this.createEntry(added));
  }

  createEntry(defaultText: DefaultText): FormGroup {
    return this.fb.group({
      id: defaultText.id,
      type: CableInfoType[defaultText.type],
      text: defaultText.text
    });
  }

  confirm() {
    this.dialogRef.close();
  }

  cancel() {
    this.dialogRef.close('Peruttu');
  }
}
