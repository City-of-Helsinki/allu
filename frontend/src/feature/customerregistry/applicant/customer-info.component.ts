import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ApplicantType} from '../../../model/application/applicant/applicant-type';
import {EnumUtil} from '../../../util/enum.util';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {Subscription} from 'rxjs/Subscription';
import {NumberUtil} from '../../../util/number.util';
import {Applicant} from '../../../model/application/applicant/applicant';
import {CustomerHub} from '../../../service/customer/customer-hub';
import {ApplicantForm} from '../../application/info/applicant/applicant.form';
import {ComplexValidator} from '../../../util/complex-validator';

const REGISTRY_KEY_VALIDATORS = [Validators.required, Validators.minLength(2)];
const PERSON_REGISTRY_KEY_VALIDATORS = [Validators.required, Validators.minLength(2), ComplexValidator.invalidSsnWarning];

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
  typeSubscription: Subscription;
  registryKeyControl: FormControl;

  private nameControl: FormControl;
  private typeControl: FormControl;

  constructor(private customerHub: CustomerHub) {}

  ngOnInit() {
    this.nameControl = <FormControl>this.form.get('name');
    this.typeControl = <FormControl>this.form.get('type');
    this.registryKeyControl = <FormControl>this.form.get('registryKey');

    this.matchingCustomers = this.nameControl.valueChanges
      .debounceTime(300)
      .switchMap(name => this.onNameSearchChange(name));

    this.typeSubscription = this.typeControl.valueChanges
      .map((type: string) => ApplicantType[type])
      .subscribe(type => this.updateRegistryKeyValidators(type));
  }

  ngOnDestroy(): void {
    this.typeSubscription.unsubscribe();
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

  private updateRegistryKeyValidators(type: ApplicantType): void {
    if (type === ApplicantType.PERSON) {
      this.registryKeyControl.setValidators(PERSON_REGISTRY_KEY_VALIDATORS);
    } else {
      this.registryKeyControl.setValidators(REGISTRY_KEY_VALIDATORS);
    }
  }
}
