import {Component, Input, OnInit} from '@angular/core';
import {UntypedFormArray, UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {MatLegacyDialog as MatDialog, MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {forkJoin} from 'rxjs';

import {DefaultTextType} from '@model/application/default-text-type';
import {findTranslation, translations} from '@util/translations';
import {ComplexValidator} from '@util/complex-validator';
import {DefaultText, DefaultTextMap} from '@model/application/cable-report/default-text';
import {DEFAULT_TEXT_MODAL_CONFIG, DefaultTextModalComponent} from '../../default-text/default-text-modal.component';
import {CableReport} from '@model/application/cable-report/cable-report';
import {Some} from '@util/option';
import {NotificationService} from '@feature/notification/notification.service';
import {ApplicationType} from '@model/application/type/application-type';
import {DefaultTextService} from '@service/application/default-text.service';
import {switchMap} from 'rxjs/internal/operators';
import {FormUtil} from '@util/form.util';

@Component({
  selector: 'cable-info',
  templateUrl: './cable-info.component.html',
  styles: [
    'h3 {margin-bottom: 2px}'
  ]
})
export class CableInfoComponent implements OnInit {
  @Input() parentForm: UntypedFormGroup;
  @Input() readonly: boolean;
  @Input() cableReport: CableReport;

  cableInfoEntries: UntypedFormArray;
  translations = translations;
  cableInfoTypes = [
    DefaultTextType.TELECOMMUNICATION, DefaultTextType.ELECTRICITY, DefaultTextType.WATER_AND_SEWAGE,
    DefaultTextType.DISTRICT_HEATING_COOLING, DefaultTextType.GAS, DefaultTextType.UNDERGROUND_STRUCTURE,
    DefaultTextType.TRAMWAY, DefaultTextType.STREET_HEATING, DefaultTextType.GEO_HEATING, DefaultTextType.SEWAGE_PIPE,
    DefaultTextType.GEOTHERMAL_WELL, DefaultTextType.GEOTECHNICAL_OBSERVATION_POST, DefaultTextType.OTHER];
  defaultTexts: DefaultTextMap = {};
  dialogRef: MatDialogRef<DefaultTextModalComponent>;

  constructor(private defaultTextService: DefaultTextService,
              private fb: UntypedFormBuilder,
              private dialog: MatDialog,
              private notification: NotificationService) {
  }

  ngOnInit(): void {
    this.cableInfoEntries = Some(this.cableInfoEntries).orElse(this.fb.array([]));
    this.initForm();
    this.defaultTextService.load(ApplicationType.CABLE_REPORT).subscribe(texts => this.setDefaultTexts(texts));
  }

  isSelected(type: DefaultTextType) {
    return this.parentForm.getRawValue().selectedCableInfoTypes.indexOf(type) >= 0;
  }

  addDefaultText(entryIndex: number, text: string) {
    let textValue = this.cableInfoEntries.at(entryIndex).get('additionalInfo').value;
    textValue = textValue + ' ' + text;
    this.cableInfoEntries.at(entryIndex).get('additionalInfo').patchValue(textValue);
  }

  editDefaultTexts(type: DefaultTextType) {
    this.dialogRef = this.dialog.open<DefaultTextModalComponent>(DefaultTextModalComponent, DEFAULT_TEXT_MODAL_CONFIG);
    const comp = this.dialogRef.componentInstance;
    comp.type = type;
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
    const cableInfoForm = this.fb.group({
      selectedCableInfoTypes: [this.cableReport.infoEntries.map(entry => entry.type)],
      mapExtractCount: [Some(this.cableReport.mapExtractCount).orElse(0), ComplexValidator.greaterThanOrEqual(0)]
    });

    this.cableInfoEntries = this.fb.array([]);
    this.cableInfoTypes.forEach(type => this.cableInfoEntries.push(this.createCableInfoEntry(type)));

    cableInfoForm.addControl('cableInfoEntries', this.cableInfoEntries);
    FormUtil.addControls(this.parentForm, cableInfoForm.controls);

    if (this.readonly) {
      cableInfoForm.disable();
    }
  }

  private createCableInfoEntry(type: DefaultTextType) {
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
