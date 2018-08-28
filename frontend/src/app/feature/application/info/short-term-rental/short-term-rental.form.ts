import {TimePeriod} from '../time-period';
import {ShortTermRental} from '../../../../model/application/short-term-rental/short-term-rental';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';
import {FormBuilder, Validators} from '@angular/forms';
import {ComplexValidator} from '../../../../util/complex-validator';
import {NumberUtil} from '../../../../util/number.util';

export class ShortTermRentalForm implements ApplicationForm {
  constructor(
    public name?: string,
    public description?: string,
    public rentalTimes?: TimePeriod,
    public commercial?: boolean,
    public billableSalesArea?: boolean,
    public calculatedPrice?: number,
    public TERMS?: string) {}

  static from(application: Application, rental: ShortTermRental): ShortTermRentalForm {
    return new ShortTermRentalForm(
      application.name,
      rental.description,
      new TimePeriod(application.startTime, application.endTime),
      rental.commercial,
      rental.billableSalesArea,
      NumberUtil.toEuros(application.calculatedPrice),
      rental.terms
    );
  }

  static to(form: ShortTermRentalForm): ShortTermRental {
    const rental =  new ShortTermRental();
    rental.description = form.description;
    rental.commercial = form.commercial;
    rental.billableSalesArea = form.billableSalesArea;
    rental.terms = form.TERMS;
    return rental;
  }

  static createStructure(fb: FormBuilder): { [key: string]: any; } {
    return {
      name: ['', [Validators.required, Validators.minLength(2)]],
      description: [''],
      commercial: [false],
      billableSalesArea: [false],
      calculatedPrice: [0],
      rentalTimes: fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') })
    };
  }

  static createDraftStructure(fb: FormBuilder): { [key: string]: any; } {
    const form = ShortTermRentalForm.createStructure(fb);
    form.description = [''];
    form.rentalTimes = fb.group({
      startTime: [undefined, Validators.required],
      endTime: [undefined, Validators.required]
    }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') });
    return form;
  }
}
