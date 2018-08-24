import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {ApplicationType} from '../../../model/application/type/application-type';
import {MatDialog, MatDialogRef} from '@angular/material';
import {forkJoin} from 'rxjs';
import {DEFAULT_TEXT_MODAL_CONFIG, DefaultTextModalComponent} from '../default-text/default-text-modal.component';
import {DefaultText} from '../../../model/application/cable-report/default-text';
import {NotificationService} from '../../../service/notification/notification.service';
import {DefaultTextType} from '../../../model/application/default-text-type';
import {Some} from '../../../util/option';
import {findTranslation} from '../../../util/translations';
import {DefaultTextService} from '../../../service/application/default-text.service';
import {switchMap} from 'rxjs/internal/operators';

@Component({
  selector: 'default-text',
  templateUrl: './default-text.component.html',
  styleUrls: []
})
export class DefaultTextComponent implements OnInit {

  @Input() form: FormGroup;
  @Input() texts: string;
  @Input() applicationType: ApplicationType;
  @Input() readonly: boolean;
  @Input() textType: string;
  @Input() includeTypes: Array<string>;

  defaultTexts: Array<DefaultText> = [];

  private textsControl: FormControl;
  private dialogRef: MatDialogRef<DefaultTextModalComponent>;

  constructor(private fb: FormBuilder,
              private dialog: MatDialog,
              private defaultTextService: DefaultTextService,
              private notification: NotificationService) {}

  ngOnInit(): void {
    this.textsControl = this.fb.control({value: this.texts, disabled: this.readonly});
    this.form.addControl(this.textType, this.textsControl);
    this.defaultTextService.load(this.applicationType).subscribe(
      dts => this.defaultTexts = this.filterDefaultTexts(dts),
      err => this.notification.errorInfo(err)
    );
  }

  addDefaultText(text: string) {
    let textValue = this.textsControl.value;
    textValue = Some(textValue).map(tv => tv + ' ' + text).orElse(text);
    this.textsControl.patchValue(textValue);
  }

  editDefaultTexts() {
    this.dialogRef = this.dialog.open<DefaultTextModalComponent>(DefaultTextModalComponent, DEFAULT_TEXT_MODAL_CONFIG);
    const comp = this.dialogRef.componentInstance;
    comp.type = DefaultTextType[this.textType];
    comp.applicationType = this.applicationType;

    this.dialogRef.afterClosed().subscribe((defaultTexts: Array<DefaultText>) => {
      this.dialogRef = undefined;

      forkJoin(defaultTexts.map(dt => this.defaultTextService.save(dt))).pipe(
        switchMap(() => this.defaultTextService.load(this.applicationType))
      ).subscribe(
        texts => {
          this.defaultTexts = this.filterDefaultTexts(texts);
          this.notification.success(findTranslation('defaultText.actions.saved'));
        },
        err => this.notification.errorInfo(err));
    });
  }

  private filterDefaultTexts(texts: Array<DefaultText>): Array<DefaultText> {
    if (this.includeTypes) {
      return texts.filter(dt => this.includeTypes.includes(DefaultTextType[dt.type]));
    }
    return texts;
  }
}
