import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

export interface FieldLabels {
  [field: string]: string;
}

export interface FieldValues {
  [field: string]: any;
}

@Component({
  selector: 'field-group-acceptance',
  templateUrl: './field-group-acceptance.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FieldGroupAcceptanceComponent implements OnInit {
  @Input() fieldLabels: FieldLabels;
  @Input() oldValues: FieldValues;
  @Input() newValues: FieldValues;
  @Input() form: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    Object.keys(this.fieldLabels).forEach(field => {
      const ctrl = this.fb.control(undefined, Validators.required);
      this.form.addControl(field, ctrl);
    });
  }
}
