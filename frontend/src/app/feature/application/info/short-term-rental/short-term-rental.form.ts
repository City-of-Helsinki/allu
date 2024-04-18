import {TimePeriod} from '../time-period';
import {ShortTermRental} from '@model/application/short-term-rental/short-term-rental';
import {Application} from '@model/application/application';
import {ApplicationForm} from '@feature/application/info/application-form';
import {UntypedFormBuilder, Validators} from '@angular/forms';
import {ComplexValidator} from '@util/complex-validator';
import {MAX_YEAR, MIN_YEAR, TimeUtil} from '@util/time.util';

export interface ShortTermRentalForm extends ApplicationForm {
  description?: string;
  rentalTimes?: TimePeriod;
  commercial?: boolean;
  billableSalesArea?: boolean;
  terms?: string;
  recurringEndYear?: number;
}

export function from(application: Application, rental: ShortTermRental): ShortTermRentalForm {
  return {
    name: application.name,
    description: rental.description,
    rentalTimes: new TimePeriod(application.startTime, application.endTime),
    commercial: rental.commercial,
    billableSalesArea: rental.billableSalesArea,
    terms: rental.terms,
    recurringEndYear: TimeUtil.yearFromDate(application.recurringEndTime)
  };
}

export function to(form: ShortTermRentalForm): ShortTermRental {
  const rental =  new ShortTermRental();
  rental.description = form.description;
  rental.commercial = form.commercial;
  rental.billableSalesArea = form.billableSalesArea;
  rental.terms = form.terms;
  return rental;
}

export function createStructure(fb: UntypedFormBuilder): { [key: string]: any; } {
  return {
    name: ['', [Validators.required, Validators.minLength(2)]],
    description: [''],
    commercial: [false],
    billableSalesArea: [false],
    rentalTimes: fb.group({
      startTime: [undefined, Validators.required],
      endTime: [undefined, Validators.required]
    }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
    terms: [undefined],
    recurringEndYear: [undefined, ComplexValidator.betweenOrEmpty(MIN_YEAR, MAX_YEAR)]
  };
}

export function createDraftStructure(fb: UntypedFormBuilder): { [key: string]: any; } {
  const form = createStructure(fb);
  form.description = [''];
  form.rentalTimes = fb.group({
    startTime: [undefined, Validators.required],
    endTime: [undefined, Validators.required]
  }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') });
  return form;
}
