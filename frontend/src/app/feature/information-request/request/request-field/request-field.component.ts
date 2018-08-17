import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'request-field',
  templateUrl: './request-field.component.html',
  styleUrls: ['./request-field.component.scss']
})
export class RequestFieldComponent implements OnInit {

  @Input() form: FormGroup;
  @Input() label: string;
  @Input() fieldKey: string;

//  constructor() { }

  ngOnInit() {
  }

  checked(): boolean {
    return this.form.value['selected' + this.fieldKey];
  }
}
