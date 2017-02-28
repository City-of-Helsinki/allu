import {Component, OnInit, Input} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';

@Component({
  selector: 'terms',
  template: require('./terms.component.html'),
  styles: [
  ]
})
export class TermsComponent implements OnInit {

  @Input() form: FormGroup;
  @Input() terms: string;
  @Input() readonly: boolean;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.form.addControl('terms', this.fb.control({value: this.terms, disabled: this.readonly}));
  }
}
