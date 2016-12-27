import {Component, OnInit, Input} from '@angular/core';
import {MdDialogRef} from '@angular/material';
import {Observable} from 'rxjs/Observable';
import {FormGroup, FormBuilder, FormArray} from '@angular/forms';

import {ApplicationHub} from '../../../service/application/application-hub';
import {DefaultText, CableInfoText} from '../../../model/application/cable-report/default-text';
import {CableInfoType} from '../../../model/application/cable-report/cable-info-type';
import {translations} from '../../../util/translations';
import {Some} from '../../../util/option';
import {StringUtil} from '../../../util/string.util';

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
      .subscribe(texts => texts.forEach(text => this.add(text)));

    this.typeName = CableInfoType[this.type];
  }

  add(defaultText: DefaultText) {
    let added = defaultText ? defaultText : DefaultText.ofType(this.type);
    this.defaultTexts.push(this.createEntry(added));
  }

  remove(index, text: CableInfoText) {
    Some(text.id)
      .do(id => this.applicationHub.removeDefaultText(id)
        .subscribe(result => console.log(result)));

    this.defaultTexts.removeAt(index);
  }

  createEntry(defaultText: DefaultText): FormGroup {
    return this.fb.group({
      id: defaultText.id,
      type: CableInfoType[defaultText.type],
      text: defaultText.text
    });
  }

  confirm() {
    let changed =  this.defaultTexts.controls
      .filter(c => c.dirty)
      .map(c => c.value)
      .filter(value => !StringUtil.isEmpty(value.text));

    this.dialogRef.close(changed);
  }

  cancel() {
    this.dialogRef.close([]);
  }
}
