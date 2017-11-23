import {
  AfterContentInit, Component, ContentChild, Directive, ElementRef, HostBinding, HostListener, Input,
  ViewEncapsulation
} from '@angular/core';

@Directive({
  selector: 'input[inputBoxInput], select[inputBoxInput]'
})
export class InputBoxInputDirective {
  focused = false;

  constructor(private _elementRef: ElementRef) {}

  focus() { this._elementRef.nativeElement.focus(); }

  @HostListener('focus') _onFocus() { this.focused = true; }

  @HostListener('blur') _onBlur() { this.focused = false; }
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

  @HostBinding('class.input-box-focused') focused = false;

  constructor() { }

  ngAfterContentInit() {
    if (!this._inputChild) {
      throw new Error('Input box requires input with attribute inputBoxInput inside it');
    }
    this.focused = this._inputChild.focused;
  }
}
