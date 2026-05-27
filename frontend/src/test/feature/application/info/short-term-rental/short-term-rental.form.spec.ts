import {UntypedFormBuilder} from '@angular/forms';
import {Application} from '@model/application/application';
import {ShortTermRental} from '@model/application/short-term-rental/short-term-rental';
import {from, to, createStructure, createDraftStructure} from '@feature/application/info/short-term-rental/short-term-rental.form';

describe('short-term-rental.form', () => {
  const REGISTRATION_NUMBERS = 'ABC-123\nXYZ-456';

  function createApplication(): Application {
    const app = new Application();
    app.name = 'Test rental';
    return app;
  }

  function createRental(registrationNumbers?: string): ShortTermRental {
    const rental = new ShortTermRental();
    rental.description = 'Kuvaus';
    rental.commercial = false;
    rental.billableSalesArea = true;
    rental.registrationNumbers = registrationNumbers;
    return rental;
  }

  describe('from()', () => {
    it('should map registrationNumbers from rental to form', () => {
      const rental = createRental(REGISTRATION_NUMBERS);
      const result = from(createApplication(), rental);
      expect(result.registrationNumbers).toBe(REGISTRATION_NUMBERS);
    });

    it('should map undefined registrationNumbers as undefined', () => {
      const rental = createRental(undefined);
      const result = from(createApplication(), rental);
      expect(result.registrationNumbers).toBeUndefined();
    });

    it('should map other fields alongside registrationNumbers', () => {
      const rental = createRental(REGISTRATION_NUMBERS);
      const result = from(createApplication(), rental);
      expect(result.description).toBe(rental.description);
      expect(result.commercial).toBe(rental.commercial);
      expect(result.billableSalesArea).toBe(rental.billableSalesArea);
      expect(result.registrationNumbers).toBe(REGISTRATION_NUMBERS);
    });
  });

  describe('to()', () => {
    it('should map registrationNumbers from form to rental', () => {
      const formValue = {registrationNumbers: REGISTRATION_NUMBERS};
      const result = to(formValue);
      expect(result.registrationNumbers).toBe(REGISTRATION_NUMBERS);
    });

    it('should map undefined registrationNumbers from form to rental', () => {
      const result = to({registrationNumbers: undefined});
      expect(result.registrationNumbers).toBeUndefined();
    });

    it('should map other fields alongside registrationNumbers', () => {
      const formValue = {
        description: 'Kuvaus',
        commercial: true,
        billableSalesArea: false,
        registrationNumbers: REGISTRATION_NUMBERS
      };
      const result = to(formValue);
      expect(result.description).toBe('Kuvaus');
      expect(result.commercial).toBe(true);
      expect(result.billableSalesArea).toBe(false);
      expect(result.registrationNumbers).toBe(REGISTRATION_NUMBERS);
    });
  });

  describe('createStructure()', () => {
    it('should include registrationNumbers control', () => {
      const fb = new UntypedFormBuilder();
      const structure = createStructure(fb);
      expect(structure.registrationNumbers).toBeDefined();
    });

    it('should initialize registrationNumbers with empty string', () => {
      const fb = new UntypedFormBuilder();
      const structure = createStructure(fb);
      const fb2 = new UntypedFormBuilder();
      const group = fb2.group(structure);
      expect(group.get('registrationNumbers').value).toBe('');
    });
  });

  describe('createDraftStructure()', () => {
    it('should include registrationNumbers control', () => {
      const fb = new UntypedFormBuilder();
      const structure = createDraftStructure(fb);
      expect(structure.registrationNumbers).toBeDefined();
    });
  });
});

