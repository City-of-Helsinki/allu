import {Component, Input, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {MatDialog, MatDialogRef} from '@angular/material';
import {forkJoin, Observable} from 'rxjs';

import {EnumUtil} from '../../../../util/enum.util';
import {DefaultTextType} from '../../../../model/application/default-text-type';
import {findTranslation, translations} from '../../../../util/translations';
import {ComplexValidator} from '../../../../util/complex-validator';
import {DefaultText, DefaultTextMap} from '../../../../model/application/cable-report/default-text';
import {DEFAULT_TEXT_MODAL_CONFIG, DefaultTextModalComponent} from '../../default-text/default-text-modal.component';
import {CableReport} from '../../../../model/application/cable-report/cable-report';
import {Some} from '../../../../util/option';
import {NotificationService} from '../../../notification/notification.service';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {DefaultTextService} from '../../../../service/application/default-text.service';
import {switchMap} from 'rxjs/internal/operators';

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
  cableInfoTypes = ['TELECOMMUNICATION', 'ELECTRICITY', 'WATER_AND_SEWAGE', 'DISTRICT_HEATING_COOLING',
                    'GAS', 'UNDERGROUND_STRUCTURE', 'TRAMWAY', 'STREET_HEATING', 'SEWAGE_PIPE',
                    'GEOTHERMAL_WELL', 'GEOTECHNICAL_OBSERVATION_POST', 'OTHER'];
  defaultTexts: DefaultTextMap = {};
  dialogRef: MatDialogRef<DefaultTextModalComponent>;

  constructor(private defaultTextService: DefaultTextService,
              private fb: FormBuilder,
              private dialog: MatDialog,
              private notification: NotificationService) {
  }

  ngOnInit(): void {
    this.cableInfoEntries = Some(this.cableInfoEntries).orElse(this.fb.array([]));
    this.initForm();
    this.defaultTextService.load(ApplicationType.CABLE_REPORT).subscribe(texts => this.setDefaultTexts(texts));

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
    const comp = this.dialogRef.componentInstance;
    comp.type = DefaultTextType[type];
    comp.applicationType = ApplicationType.CABLE_REPORT;

    this.dialogRef.afterClosed().subscribe((defaultTexts: Array<DefaultText>) => {
      this.dialogRef = undefined;

      forkJoin(defaultTexts.map(dt => this.defaultTextService.save(dt))).pipe(
        switchMap(result => this.defaultTextService.load(ApplicationType.CABLE_REPORT))
      ).subscribe(
        texts => {
          this.setDefaultTexts(texts);
          this.notification.success(findTranslation('defaultText.actions.saved'));
        },
        err => this.notification.errorInfo(err));
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
    const additionalInfo = Some(this.cableReport.infoEntries.find(entry => entry.type === type))
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
