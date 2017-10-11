import {Component, Input, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';

import {ApplicationHub} from '../../../service/application/application-hub';
import {DefaultText} from '../../../model/application/cable-report/default-text';
import {DefaultTextType} from '../../../model/application/default-text-type';
import {translations} from '../../../util/translations';
import {StringUtil} from '../../../util/string.util';
import {NotificationService} from '../../../service/notification/notification.service';
import {ApplicationType} from '../../../model/application/type/application-type';
import {NumberUtil} from '../../../util/number.util';

export const DEFAULT_TEXT_MODAL_CONFIG = {disableClose: false, width: '800px'};

@Component({
  selector: 'default-text-modal',
  template: require('./default-text-modal.component.html'),
  styles: []
})
export class DefaultTextModalComponent implements OnInit {
  @Input() applicationType: ApplicationType;
  @Input() type: DefaultTextType;

  typeName: string;
  defaultTextform: FormGroup;
  defaultTexts: FormArray;
  changed = new Set<number>();
  translations = translations;

  constructor(public dialogRef: MatDialogRef<DefaultTextModalComponent>,
              private fb: FormBuilder,
              private applicationHub: ApplicationHub) {
    this.defaultTexts = fb.array([]);
    this.defaultTextform = fb.group({
      defaultTexts: this.defaultTexts
    });
  }

  ngOnInit(): void {
    this.applicationHub.loadDefaultTexts(this.applicationType)
      .map(texts => texts.filter(text => text.type === this.type))
      .subscribe(
        texts => texts.forEach(text => this.add(text)),
        error => NotificationService.error(error));

    this.typeName = DefaultTextType[this.type];
  }

  add(defaultText: DefaultText) {
    let added = defaultText ? defaultText : DefaultText.ofType(this.applicationType, this.type);
    this.defaultTexts.push(this.createEntry(added));
  }

  remove(index, text: DefaultText) {
    if (NumberUtil.isDefined(text.id)) {
      this.applicationHub.removeDefaultText(text.id)
        .subscribe(
          result => this.defaultTexts.removeAt(index),
          error => NotificationService.error(error)
        );
    } else {
      this.defaultTexts.removeAt(index);
    }
  }

  createEntry(defaultText: DefaultText): FormGroup {
    return this.fb.group({
      id: defaultText.id,
      applicationType: ApplicationType[defaultText.applicationType],
      type: DefaultTextType[defaultText.type],
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
