import {Directive, Input, OnInit} from '@angular/core';
import {AbstractControlWarn} from '../../../util/complex-validator';
@Directive({
  selector: 'mat-form-field[inputWarning]',
  host: {
    '[class.has-warning]': 'warning'
  }
})
export class InputWarningDirective implements OnInit {
  @Input('inputWarning') control: AbstractControlWarn;

  constructor() {}

  ngOnInit(): void {
    if (!this.control) {
      throw new Error('Input warning requires control as input');
    }
  }

  get warning(): boolean {
    const warnings = this.control.warnings;
    return warnings && Object.keys(warnings).some(key => warnings[key] !== undefined);
  }
}
