import {Component, Input, OnInit} from '@angular/core';
import {FormGroup, FormBuilder, FormArray} from '@angular/forms';
import {MatDialog, MatDialogRef} from '@angular/material';
import {Observable} from 'rxjs/Observable';

import {EnumUtil} from '../../../../util/enum.util';
import {DefaultTextType} from '../../../../model/application/default-text-type';
import {translations, findTranslation} from '../../../../util/translations';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {ComplexValidator} from '../../../../util/complex-validator';
import {DefaultText, DefaultTextMap} from '../../../../model/application/cable-report/default-text';
import {DefaultTextModalComponent, DEFAULT_TEXT_MODAL_CONFIG} from '../../default-text/default-text-modal.component';
import {CableReport} from '../../../../model/application/cable-report/cable-report';
import {Some} from '../../../../util/option';
import {NotificationService} from '../../../../service/notification/notification.service';
import {ApplicationType} from '../../../../model/application/type/application-type';

@Component({
  selector: 'cable-info',
  templateUrl: './cable-info.component.html'
})
export class CableInfoComponent implements OnInit {
  @Input() parentForm: FormGroup;
  @Input() readonly: boolean;
  @Input() cableReport: CableReport;

  cableInfoForm: FormGroup;
  cableInfoEntries: FormArray;
  translations = translations;
  cableInfoTypes = EnumUtil.enumValues(DefaultTextType);
  defaultTexts: DefaultTextMap = {};
  dialogRef: MatDialogRef<DefaultTextModalComponent>;

  constructor(private applicationHub: ApplicationHub,
              private fb: FormBuilder,
              private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.cableInfoEntries = Some(this.cableInfoEntries).orElse(this.fb.array([]));
    this.initForm();
    this.applicationHub.loadDefaultTexts(ApplicationType.CABLE_REPORT).subscribe(texts => this.setDefaultTexts(texts));

    if (this.readonly) {
      this.cableInfoForm.disable();
    }
  }

  isSelected(type: string) {
    return this.cableInfoForm.value.selectedCableInfoTypes.indexOf(DefaultTextType[type]) >= 0;
  }

  addDefaultText(entryIndex: number, text: string) {
    let textValue = this.cableInfoEntries.at(entryIndex).get('additionalInfo').value;
    textValue = textValue + ' ' + text;
    this.cableInfoEntries.at(entryIndex).get('additionalInfo').patchValue(textValue);
  }

  editDefaultTexts(type: string) {
    this.dialogRef = this.dialog.open<DefaultTextModalComponent>(DefaultTextModalComponent, DEFAULT_TEXT_MODAL_CONFIG);
    let comp = this.dialogRef.componentInstance;
    comp.type = DefaultTextType[type];
    comp.applicationType = ApplicationType.CABLE_REPORT;

    this.dialogRef.afterClosed().subscribe((defaultTexts: Array<DefaultText>) => {
      this.dialogRef = undefined;

      Observable.forkJoin(defaultTexts.map(dt => this.applicationHub.saveDefaultText(dt)))
        .switchMap(result => this.applicationHub.loadDefaultTexts(ApplicationType.CABLE_REPORT))
        .subscribe(
          texts => {
            this.setDefaultTexts(texts);
            NotificationService.message(findTranslation('defaultText.actions.saved'));
          },
          err => NotificationService.error(err));
    });
  }

  private initForm(): void {
    this.cableInfoForm = this.fb.group({
      selectedCableInfoTypes: [this.cableReport.infoEntries.map(entry => entry.type)],
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
