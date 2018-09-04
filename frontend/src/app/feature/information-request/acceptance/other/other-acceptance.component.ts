import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {Application} from '@model/application/application';
import {FormBuilder, FormGroup} from '@angular/forms';
import {FieldValues} from '@feature/information-request/acceptance/field-group-acceptance.component';
import {InformationRequestFieldKey, OtherInfoKeys} from '@model/information-request/information-request-field-key';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {SetOtherInfo} from '@feature/information-request/actions/information-request-result-actions';

@Component({
  selector: 'other-acceptance',
  templateUrl: './other-acceptance.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OtherAcceptanceComponent implements OnInit {
  @Input() parentForm: FormGroup;
  @Input() oldInfo: Application;
  @Input() newInfo: Application;
  @Input() readonly: boolean;
  @Input() fieldKeys: string[];

  form: FormGroup;
  otherInfoKeys: string[] = [];

  constructor(private fb: FormBuilder, private store: Store<fromRoot.State>) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.parentForm.addControl('other', this.form);
    this.otherInfoKeys = this.fieldKeys
      .filter(key => OtherInfoKeys.some(otherInfoKey => otherInfoKey ===  InformationRequestFieldKey[key]));
  }

  otherInfoChanges(fieldValues: FieldValues): void {
    this.store.dispatch(new SetOtherInfo(fieldValues));
  }
}
