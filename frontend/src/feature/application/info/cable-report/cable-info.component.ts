import {Component, Input, OnInit, ViewContainerRef} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, FormControl, FormArray, Validators} from '@angular/forms';
import {MdDialog, MdDialogRef, MdDialogConfig} from '@angular/material';

import {EnumUtil} from '../../../../util/enum.util.ts';
import {CableInfoType} from '../../../../model/application/cable-report/cable-info-type';
import {StructureMeta} from '../../../../model/application/structure-meta';
import {translations} from '../../../../util/translations';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {MaterializeUtil} from '../../../../util/materialize.util.ts';
import {DefaultText} from '../../../../model/application/cable-report/default-text';
import {DefaultTextModalComponent} from '../../default-text/default-text-modal.component';

@Component({
  selector: 'cable-info',
  template: require('./cable-info.component.html')
})
export class CableInfoComponent {
  @Input() parentForm: FormGroup;
  @Input() readonly: boolean;

  cableInfoForm: FormGroup;
  cableInfoEntries: FormArray;
  meta: StructureMeta;
  translations = translations;
  cableInfoTypes = EnumUtil.enumValues(CableInfoType);
  selectedCableInfoTypes = [];
  defaultTexts: Array<DefaultText>;
  dialogRef: MdDialogRef<DefaultTextModalComponent>;

  constructor(private applicationHub: ApplicationHub,
              private route: ActivatedRoute,
              private fb: FormBuilder,
              private viewContainerRef: ViewContainerRef,
              private dialog: MdDialog) {
  }

  ngOnInit(): void {
    this.initForm();

    this.route.parent.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => {
        // this.applicationHub.loadMetaData(application.type).subscribe(meta => this.meta = meta);
      });

    this.applicationHub.loadDefaultTexts().subscribe(texts => this.defaultTexts = texts);
  }

  isSelected(type: string) {
    return this.selectedCableInfoTypes.indexOf(CableInfoType[type]) >= 0;
  }

  defaultTextsFor(type: string) {
    return this.defaultTexts.filter(text => text.type === CableInfoType[type]);
  }

  addDefaultText(entryIndex: number, text: string) {
    let textValue = this.cableInfoEntries.at(entryIndex).get('additionalInfo').value;
    textValue = textValue + ' ' + text;
    this.cableInfoEntries.at(entryIndex).get('additionalInfo').patchValue(textValue);
    MaterializeUtil.updateTextFields(50);
  }

  editDefaultTexts(type: string) {
    this.dialogRef = this.dialog.open(DefaultTextModalComponent, {disableClose: false});
    this.dialogRef.componentInstance.type = CableInfoType[type];

    this.dialogRef.afterClosed().subscribe(dialogCloseValue => {
      this.dialogRef = undefined;
    });
  }

  private initForm(): void {
    this.cableInfoForm = this.fb.group({
      mapExtractCount: [0, ComplexValidator.greaterThanOrEqual(0)]
    });

    this.cableInfoEntries = this.fb.array([]);
    this.cableInfoTypes.forEach(type => this.cableInfoEntries.push(this.createCableInfoEntry(type)));

    this.cableInfoForm.addControl('cableInfoEntries', this.cableInfoEntries);
    this.parentForm.addControl('cableInfo', this.cableInfoForm);
  }

  private createCableInfoEntry(type: string) {
    return this.fb.group({
      cableInfoType: [type],
      additionalInfo: ['']
    });
  }
}
