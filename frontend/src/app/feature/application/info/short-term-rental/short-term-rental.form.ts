import {TimePeriod} from '../time-period';
import {ShortTermRental} from '../../../../model/application/short-term-rental/short-term-rental';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';
import {FormBuilder, Validators} from '@angular/forms';
import {ComplexValidator} from '../../../../util/complex-validator';

export class ShortTermRentalForm implements ApplicationForm {
  constructor(
    public name?: string,
    public description?: string,
    public rentalTimes?: TimePeriod,
    public commercial?: boolean,
    public largeSalesArea?: boolean,
    public calculatedPrice?: number,
    public terms?: string) {}

  static from(application: Application, rental: ShortTermRental): ShortTermRentalForm {
    return new ShortTermRentalForm(
      application.name,
      rental.description,
      new TimePeriod(application.startTime, application.endTime),
      rental.commercial,
      rental.largeSalesArea,
      application.calculatedPriceEuro,
      rental.terms
    );
  }

  static to(form: ShortTermRentalForm): ShortTermRental {
    const rental =  new ShortTermRental();
    rental.description = form.description;
    rental.commercial = form.commercial;
    rental.largeSalesArea = form.largeSalesArea;
    rental.terms = form.terms;
    return rental;
  }

  static createStructure(fb: FormBuilder): { [key: string]: any; } {
    return {
      name: ['', [Validators.required, Validators.minLength(2)]],
      description: [''],
      commercial: [false],
      largeSalesArea: [false],
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
