import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {ApplicationType} from '../../../model/application/type/application-type';
import {MatDialog, MatDialogRef} from '@angular/material';
import {Observable} from 'rxjs/Observable';
import {DEFAULT_TEXT_MODAL_CONFIG, DefaultTextModalComponent} from '../default-text/default-text-modal.component';
import {DefaultText} from '../../../model/application/cable-report/default-text';
import {NotificationService} from '../../../service/notification/notification.service';
import {DefaultTextType} from '../../../model/application/default-text-type';
import {Some} from '../../../util/option';
import {findTranslation} from '../../../util/translations';
import {DefaultTextService} from '../../../service/application/default-text.service';

@Component({
  selector: 'terms',
  templateUrl: './terms.component.html',
  styleUrls: []
})
export class TermsComponent implements OnInit {

  @Input() form: FormGroup;
  @Input() terms: string;
  @Input() applicationType: ApplicationType;
  @Input() readonly: boolean;

  defaultTexts: Array<DefaultText> = [];

  private termsControl: FormControl;
  private dialogRef: MatDialogRef<DefaultTextModalComponent>;

  constructor(private fb: FormBuilder,
              private dialog: MatDialog,
              private defaultTextService: DefaultTextService) {}

  ngOnInit(): void {
    this.termsControl = this.fb.control({value: this.terms, disabled: this.readonly});
    this.form.addControl('terms', this.termsControl);
    this.defaultTextService.load(this.applicationType).subscribe(
      dts => this.defaultTexts = dts,
      err => NotificationService.error(err)
    );
  }

  addDefaultText(entryIndex: number, text: string) {
    let textValue = this.termsControl.value;
    textValue = Some(textValue).map(tv => tv + ' ' + text).orElse(text);
    this.termsControl.patchValue(textValue);
  }

  editDefaultTexts() {
    this.dialogRef = this.dialog.open<DefaultTextModalComponent>(DefaultTextModalComponent, DEFAULT_TEXT_MODAL_CONFIG);
    const comp = this.dialogRef.componentInstance;
    comp.type = DefaultTextType.OTHER;
    comp.applicationType = this.applicationType;

    this.dialogRef.afterClosed().subscribe((defaultTexts: Array<DefaultText>) => {
      this.dialogRef = undefined;

      Observable.forkJoin(defaultTexts.map(dt => this.defaultTextService.save(dt)))
        .switchMap(result => this.defaultTextService.load(this.applicationType))
        .subscribe(
          texts => {
            this.defaultTexts = texts;
            NotificationService.message(findTranslation('defaultText.actions.saved'));
          },
          err => NotificationService.error(err));
    });
  }
}
