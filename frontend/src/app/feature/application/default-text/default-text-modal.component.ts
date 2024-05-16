import {Component, Input, OnInit} from '@angular/core';
import {MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {UntypedFormArray, UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';

import {DefaultText} from '../../../model/application/cable-report/default-text';
import {DefaultTextType} from '../../../model/application/default-text-type';
import {translations} from '../../../util/translations';
import {StringUtil} from '../../../util/string.util';
import {NotificationService} from '../../notification/notification.service';
import {ApplicationType} from '../../../model/application/type/application-type';
import {NumberUtil} from '../../../util/number.util';
import {DefaultTextService} from '../../../service/application/default-text.service';
import {map} from 'rxjs/internal/operators';

export const DEFAULT_TEXT_MODAL_CONFIG = {disableClose: false, width: '800px'};

@Component({
  selector: 'default-text-modal',
  templateUrl: './default-text-modal.component.html',
  styleUrls: []
})
export class DefaultTextModalComponent implements OnInit {
  @Input() applicationType: ApplicationType;
  @Input() type: DefaultTextType;

  typeName: string;
  defaultTextform: UntypedFormGroup;
  defaultTexts: UntypedFormArray;
  changed = new Set<number>();
  translations = translations;

  constructor(public dialogRef: MatDialogRef<DefaultTextModalComponent>,
              private fb: UntypedFormBuilder,
              private defaultTextService: DefaultTextService,
              private notification: NotificationService) {
    this.defaultTexts = fb.array([]);
    this.defaultTextform = fb.group({
      defaultTexts: this.defaultTexts
    });
  }

  ngOnInit(): void {
    this.defaultTextService.load(this.applicationType).pipe(
      map(texts => texts.filter(text => text.type === this.type))
    ).subscribe(
      texts => texts.forEach(text => this.add(text)),
      error => this.notification.errorInfo(error));

    this.typeName = DefaultTextType[this.type];
  }

  add(defaultText?: DefaultText) {
    const added = defaultText ? defaultText : DefaultText.ofType(this.applicationType, this.type);
    this.defaultTexts.push(this.createEntry(added));
  }

  remove(index, text: DefaultText) {
    if (NumberUtil.isDefined(text.id)) {
      this.defaultTextService.remove(text.id)
        .subscribe(
          result => this.defaultTexts.removeAt(index),
          error => this.notification.errorInfo(error)
        );
    } else {
      this.defaultTexts.removeAt(index);
    }
  }

  createEntry(defaultText: DefaultText): UntypedFormGroup {
    return this.fb.group({
      id: defaultText.id,
      applicationType: ApplicationType[defaultText.applicationType],
      type: DefaultTextType[defaultText.type],
      text: defaultText.text
    });
  }

  confirm() {
    const changed =  this.defaultTexts.controls
      .filter(c => c.dirty)
      .map(c => c.value)
      .filter(value => !StringUtil.isEmpty(value.text));

    this.dialogRef.close(changed);
  }

  cancel() {
    this.dialogRef.close([]);
  }
}
