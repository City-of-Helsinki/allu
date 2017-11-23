import {AfterContentInit, Component, ContentChild, Directive, ElementRef, Input, ViewEncapsulation} from '@angular/core';

@Directive({
  selector: 'input[inputBoxInput], select[inputBoxInput]',
  host: {
    '(blur)': '_onBlur()',
    '(focus)': '_onFocus()'
  }
})
export class InputBoxInputDirective {
  focused = false;

  constructor(private _elementRef: ElementRef) {}

  focus() { this._elementRef.nativeElement.focus(); }

  _onFocus() { this.focused = true; }

  _onBlur() { this.focused = false; }
}

@Component({
  selector: 'input-box',
  templateUrl: './input-box.component.html',
  styleUrls: [
    './input-box.component.scss'
  ],
  encapsulation: ViewEncapsulation.None,
  host: {
    '[class.input-box-focused]': '_inputChild.focused'
  }
})
export class InputBoxComponent implements AfterContentInit {
  @Input() placeholder: string;

  @ContentChild(InputBoxInputDirective) _inputChild: InputBoxInputDirective;

  constructor() { }

  ngAfterContentInit() {
    if (!this._inputChild) {
      throw new Error('Input box requires input with attribute inputBoxInput inside it');
    }
  }
}
