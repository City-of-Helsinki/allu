import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ApplicantType} from '../../../model/application/applicant/applicant-type';
import {EnumUtil} from '../../../util/enum.util';
import {FormControl, FormGroup} from '@angular/forms';
import {NumberUtil} from '../../../util/number.util';
import {Applicant} from '../../../model/application/applicant/applicant';
import {CustomerHub} from '../../../service/customer/customer-hub';
import {Observable} from 'rxjs/Observable';
import {ApplicantForm} from '../../application/info/applicant/applicant.form';

@Component({
  selector: 'customer-info',
  template: require('./customer-info.component.html'),
  styles: []
})
export class CustomerInfoComponent {
  @Input() form: FormGroup;
  @Input() allowSearch: boolean = false;

  @Output() customerChange = new EventEmitter<Applicant>();

  matchingCustomers: Observable<Array<Applicant>>;
  applicantTypes = EnumUtil.enumValues(ApplicantType);

  private nameControl: FormControl;
  private typeControl: FormControl;


  constructor(private customerHub: CustomerHub) {}

  ngOnInit() {
    this.nameControl = <FormControl>this.form.get('name');
    this.typeControl = <FormControl>this.form.get('type');

    this.matchingCustomers = this.nameControl.valueChanges
      .debounceTime(300)
      .switchMap(name => this.onNameSearchChange(name));
  }

  onNameSearchChange(term: string): Observable<Array<Applicant>> {
    if (this.allowSearch) {
      return this.customerHub.searchApplicantsBy({name: term, type: this.typeControl.value});
    } else {
      return Observable.empty();
    }
  }

  customerSelected(applicant: Applicant): void {
    this.form.patchValue(ApplicantForm.fromApplicant(applicant));
    this.customerChange.emit(applicant);
  }

  /**
   * Resets form values if form contained existing applicant
   * and form allows search
   */
  resetFormIfExisting(): void {
    if (NumberUtil.isDefined(this.form.value.id) && this.allowSearch) {
      this.form.reset({
        name: this.form.value.name,
        type: this.form.value.type,
        active: true
      });
      this.form.enable();
      this.customerChange.emit(new Applicant());
    }
  }
}
