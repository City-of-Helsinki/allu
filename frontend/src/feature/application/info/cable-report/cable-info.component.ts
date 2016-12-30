import {Component, Input} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, FormArray} from '@angular/forms';
import {MdDialog, MdDialogRef} from '@angular/material';
import {Observable} from 'rxjs/Observable';
import '../../../../rxjs-extensions';

import {EnumUtil} from '../../../../util/enum.util.ts';
import {CableInfoType} from '../../../../model/application/cable-report/cable-info-type';
import {StructureMeta} from '../../../../model/application/structure-meta';
import {translations} from '../../../../util/translations';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {ComplexValidator} from '../../../../util/complex-validator';
import {MaterializeUtil} from '../../../../util/materialize.util.ts';
import {DefaultText, DefaultTextMap} from '../../../../model/application/cable-report/default-text';
import {DefaultTextModalComponent} from '../../default-text/default-text-modal.component';
import {CableReport} from '../../../../model/application/cable-report/cable-report';
import {Some} from '../../../../util/option';

@Component({
  selector: 'cable-info',
  template: require('./cable-info.component.html')
})
export class CableInfoComponent {
  @Input() parentForm: FormGroup;
  @Input() readonly: boolean;
  @Input() cableReport: CableReport;

  cableInfoForm: FormGroup;
  cableInfoEntries: FormArray;
  meta: StructureMeta;
  translations = translations;
  cableInfoTypes = EnumUtil.enumValues(CableInfoType);
  selectedCableInfoTypes = [];
  defaultTexts: DefaultTextMap = {};
  dialogRef: MdDialogRef<DefaultTextModalComponent>;

  constructor(private applicationHub: ApplicationHub,
              private route: ActivatedRoute,
              private fb: FormBuilder,
              private dialog: MdDialog) {
  }

  ngOnInit(): void {
    this.cableInfoEntries = Some(this.cableInfoEntries).orElse([]);
    this.initForm();
    this.selectedCableInfoTypes = this.cableReport.infoEntries.map(entry => entry.type);

    this.applicationHub.loadDefaultTexts().subscribe(texts => this.setDefaultTexts(texts));

    if (this.readonly) {
      this.cableInfoForm.disable();
    }
  }

  isSelected(type: string) {
    return this.selectedCableInfoTypes.indexOf(CableInfoType[type]) >= 0;
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

    this.dialogRef.afterClosed().subscribe((defaultTexts: Array<DefaultText>) => {
      this.dialogRef = undefined;
      Observable.from(defaultTexts)
        .flatMap(text => this.applicationHub.saveDefaultText(text))
        // counts save results to get single observable from array of observables
        .reduce((saveCount, val, idx) => ++saveCount, 0)
        .subscribe(result => this.applicationHub.loadDefaultTexts()
            .subscribe(texts => this.setDefaultTexts(texts)));
    });


  }

  private initForm(): void {
    this.cableInfoForm = this.fb.group({
      mapExtractCount: [Some(this.cableReport.mapExtractCount).orElse(0), ComplexValidator.greaterThanOrEqual(0)]
    });

    this.cableInfoEntries = this.fb.array([]);
    this.cableInfoTypes.forEach(type => this.cableInfoEntries.push(this.createCableInfoEntry(type)));

    this.cableInfoForm.addControl('cableInfoEntries', this.cableInfoEntries);
    this.parentForm.addControl('cableInfo', this.cableInfoForm);
  }

  private createCableInfoEntry(type: string) {
    let additionalInfo = Some(this.cableReport.infoEntries.find(entry => entry.type === type))
      .map(entry => entry.additionalInfo)
      .orElse('') ;

    return this.fb.group({
      type: [type],
      additionalInfo: [additionalInfo]
    });
  }

  private setDefaultTexts(texts: Array<DefaultText>) {
    this.defaultTexts = DefaultText.groupByType(texts);
  }
}
