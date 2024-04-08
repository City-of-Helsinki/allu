import {
  AfterContentInit,
  Component,
  ContentChild,
  ContentChildren,
  Directive,
  ElementRef,
  HostBinding,
  HostListener,
  Input,
  QueryList,
  ViewEncapsulation
} from '@angular/core';
import {MatError} from '@angular/material/form-field';

@Directive({
  selector: 'input[inputBoxInput], select[inputBoxInput], mat-select[inputBoxInput], button[inputBoxInput], textarea[inputBoxInput]'
})
export class InputBoxInputDirective {
  focused = false;

  constructor(private _elementRef: ElementRef) {}

  focus() { this._elementRef.nativeElement.focus(); }

  @HostListener('focus') _onFocus() { this.focused = true; }

  @HostListener('blur') _onBlur() { this.focused = false; }

  get touched() {
    return this._elementRef.nativeElement.classList.contains('ng-touched');
  }

  get dirty() {
    return this._elementRef.nativeElement.classList.contains('ng-dirty');
  }
}

@Component({
  selector: 'input-box',
  templateUrl: './input-box.component.html',
  styleUrls: [
    './input-box.component.scss'
  ],
  encapsulation: ViewEncapsulation.None
})
export class InputBoxComponent implements AfterContentInit {
  @Input() placeholder: string;

  @ContentChild(InputBoxInputDirective) _inputChild: InputBoxInputDirective;

  @ContentChildren(MatError) errors: QueryList<MatError>;

  @HostBinding('class.input-box-focused') focused = false;

  constructor() { }

  ngAfterContentInit() {
    if (!this._inputChild) {
      throw new Error('Input box requires input with attribute inputBoxInput inside it');
    }
    this.focused = this._inputChild.focused;
  }

  get touched() {
    return this._inputChild.touched;
  }

  get dirty() {
    return this._inputChild.dirty;
  }
}
