import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormControl, Validators} from '@angular/forms';
import {Configuration} from '@model/config/configuration';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Save} from '@feature/admin/configuration/actions/configuration-actions';
import {ConfigurationType} from '@model/config/configuration-type';
import {TimeUtil} from '@util/time.util';

@Component({
  selector: 'configuration-date-value',
  templateUrl: './configuration-date-value.component.html',
  styleUrls: ['./configuration-value.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigurationDateValueComponent implements OnInit {

  @Input() configuration: Configuration;

  valueCtrl: UntypedFormControl;

  constructor(
    private fb: UntypedFormBuilder,
    private store: Store<fromRoot.State>
  ) {}

  ngOnInit(): void {
    const date = TimeUtil.dateFromBackend(this.configuration.value);

    this.valueCtrl = this.fb.control(date, [Validators.required]);
  }

  submit(): void {
    const configuration: Configuration = {
      ...this.configuration,
      value: TimeUtil.dateToBackend(this.valueCtrl.value)
    };
    this.store.dispatch(new Save(configuration));
  }
}
