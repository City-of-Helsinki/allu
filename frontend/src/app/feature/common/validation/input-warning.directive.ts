import {Directive, HostBinding, Input, OnDestroy, OnInit} from '@angular/core';
import {AbstractControlWarn} from '@util/complex-validator';
import {Subscription} from 'rxjs/index';
@Directive({
  selector: 'mat-form-field[inputWarning]'
})
export class InputWarningDirective implements OnInit, OnDestroy {
  @Input() inputWarning: AbstractControlWarn;

  @HostBinding('class.has-warning') warning: boolean;

  private inputSub: Subscription;

  constructor() {}

  ngOnInit(): void {
    if (!this.inputWarning) {
      throw new Error('Input warning requires control as input');
    }
    this.inputSub = this.inputWarning.valueChanges.subscribe(() => {
      const warnings = this.inputWarning.warnings;
      this.warning = warnings && Object.keys(warnings).some(key => warnings[key] !== undefined);
    });
  }

  ngOnDestroy(): void {
    this.inputSub.unsubscribe();
  }
}
