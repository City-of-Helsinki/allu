import {Component, OnInit, Input} from '@angular/core';
import {FormBuilder, FormGroup, FormControl} from '@angular/forms';
import {ApplicationType} from '../../../model/application/type/application-type';
import {MatDialogRef, MatDialog} from '@angular/material';
import {Observable} from 'rxjs/Observable';
import {DefaultTextModalComponent, DEFAULT_TEXT_MODAL_CONFIG} from '../default-text/default-text-modal.component';
import {DefaultText} from '../../../model/application/cable-report/default-text';
import {ApplicationHub} from '../../../service/application/application-hub';
import {NotificationService} from '../../../service/notification/notification.service';
import {DefaultTextType} from '../../../model/application/default-text-type';
import {Some} from '../../../util/option';
import {findTranslation} from '../../../util/translations';

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
              private applicationHub: ApplicationHub) {}

  ngOnInit(): void {
    this.termsControl = this.fb.control({value: this.terms, disabled: this.readonly});
    this.form.addControl('terms', this.termsControl);
    this.applicationHub.loadDefaultTexts(this.applicationType).subscribe(
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
    let comp = this.dialogRef.componentInstance;
    comp.type = DefaultTextType.OTHER;
    comp.applicationType = this.applicationType;

    this.dialogRef.afterClosed().subscribe((defaultTexts: Array<DefaultText>) => {
      this.dialogRef = undefined;

      Observable.forkJoin(defaultTexts.map(dt => this.applicationHub.saveDefaultText(dt)))
        .switchMap(result => this.applicationHub.loadDefaultTexts(this.applicationType))
        .subscribe(
          texts => {
            this.defaultTexts = texts;
            NotificationService.message(findTranslation('defaultText.actions.saved'));
          },
          err => NotificationService.error(err));
    });
  }
}
