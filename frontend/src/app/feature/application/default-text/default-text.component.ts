import {Attribute, Component, forwardRef, Input, OnDestroy, OnInit} from '@angular/core';
import {ControlValueAccessor, UntypedFormBuilder, UntypedFormControl, NG_VALUE_ACCESSOR, Validators} from '@angular/forms';
import {ApplicationType} from '@model/application/type/application-type';
import {MatLegacyDialog as MatDialog, MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {forkJoin, Subscription} from 'rxjs';
import {DEFAULT_TEXT_MODAL_CONFIG, DefaultTextModalComponent} from '../default-text/default-text-modal.component';
import {DefaultText} from '@model/application/cable-report/default-text';
import {NotificationService} from '@feature/notification/notification.service';
import {DefaultTextType} from '@model/application/default-text-type';
import {Some} from '@util/option';
import {findTranslation} from '@util/translations';
import {DefaultTextService} from '@service/application/default-text.service';
import {switchMap} from 'rxjs/internal/operators';

const DEFAULT_TEXT_VALUE_ACCESSOR = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => DefaultTextComponent),
  multi: true
};

@Component({
  selector: 'default-text',
  templateUrl: './default-text.component.html',
  styleUrls: [],
  providers: [DEFAULT_TEXT_VALUE_ACCESSOR]
})
export class DefaultTextComponent implements OnInit, OnDestroy, ControlValueAccessor {
  @Input() applicationType: ApplicationType;
  @Input() readonly: boolean;
  @Input() textType: string;
  @Input() includeTypes: Array<string>;

  defaultTexts: Array<DefaultText> = [];
  textsControl: UntypedFormControl;

  private changeSub: Subscription;
  private dialogRef: MatDialogRef<DefaultTextModalComponent>;

  constructor(@Attribute('required') public required: boolean = false,
              private fb: UntypedFormBuilder,
              private dialog: MatDialog,
              private defaultTextService: DefaultTextService,
              private notification: NotificationService) {}

  ngOnInit(): void {
    this.textsControl = this.fb.control(
      {value: undefined, disabled: this.readonly},
      this.required ? Validators.required : null
    );

    this.defaultTextService.load(this.applicationType).subscribe(
      dts => this.defaultTexts = this.filterDefaultTexts(dts),
      err => this.notification.errorInfo(err)
    );
    this.changeSub = this.textsControl.valueChanges.subscribe(val => {
      this._onChange(val);
      this._onTouched(val);
    });
  }

  ngOnDestroy(): void {
    this.changeSub.unsubscribe();
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

  registerOnChange(fn: any): void {
    this._onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this._onTouched = fn;
  }

  writeValue(text: string): void {
    this.textsControl.setValue(text, {emitEvent: false});
  }

  private _onChange = (_: any) => {};

  private _onTouched = (_: any) => {};

  private filterDefaultTexts(texts: Array<DefaultText>): Array<DefaultText> {
    if (this.includeTypes) {
      return texts.filter(dt => this.includeTypes.includes(DefaultTextType[dt.type]));
    }
    return texts;
  }
}
