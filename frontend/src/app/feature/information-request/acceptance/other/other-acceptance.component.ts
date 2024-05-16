import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {Application} from '@model/application/application';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {InformationRequestFieldKey, OtherInfoKeys} from '@model/information-request/information-request-field-key';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {SetOtherInfo} from '@feature/information-request/actions/information-request-result-actions';
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {Observable} from 'rxjs';
import {StructureMeta} from '@model/application/meta/structure-meta';

@Component({
  selector: 'other-acceptance',
  templateUrl: './other-acceptance.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OtherAcceptanceComponent implements OnInit {
  @Input() parentForm: UntypedFormGroup;
  @Input() oldInfo: Application;
  @Input() newInfo: Application;
  @Input() readonly: boolean;
  @Input() fieldKeys: InformationRequestFieldKey[];
  @Input() hideExisting = false;

  form: UntypedFormGroup;
  otherInfoKeys: string[] = [];
  meta$: Observable<StructureMeta>;

  constructor(private fb: UntypedFormBuilder, private store: Store<fromRoot.State>) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.parentForm.addControl('other', this.form);
    this.otherInfoKeys = this.fieldKeys
      .filter(key => OtherInfoKeys.some(otherInfoKey => otherInfoKey ===  key));
    this.meta$ = this.store.pipe(select(fromApplication.getMeta));
  }

  otherInfoChanges(fieldValues: FieldValues): void {
    this.store.dispatch(new SetOtherInfo(fieldValues));
  }
}
