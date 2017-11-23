import {Directive, HostBinding, Input, OnInit} from '@angular/core';
import {AbstractControlWarn} from '../../../util/complex-validator';
@Directive({
  selector: 'mat-form-field[inputWarning]'
})
export class InputWarningDirective implements OnInit {
  @Input() inputWarning: AbstractControlWarn;

  @HostBinding('class.has-warning') warning() {
    const warnings = this.inputWarning.warnings;
    return warnings && Object.keys(warnings).some(key => warnings[key] !== undefined);
  }

  constructor() {}

  ngOnInit(): void {
    if (!this.inputWarning) {
      throw new Error('Input warning requires control as input');
    }
  }
}
