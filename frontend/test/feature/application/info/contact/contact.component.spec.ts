import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {MdCardModule} from '@angular/material';
import {ContactComponent} from '../../../../../src/feature/application/info/contact/contact.component';
import {AlluCommonModule} from '../../../../../src/feature/common/allu-common.module';
import {ApplicationState} from '../../../../../src/service/application/application-state';
import {ApplicationStateMock, CustomerHubMock} from '../../../../mocks';
import {CustomerHub} from '../../../../../src/service/customer/customer-hub';
import {CustomerRoleType} from '../../../../../src/model/customer/customer-role-type';
import {CustomerWithContactsForm} from '../../../../../src/feature/customerregistry/customer/customer-with-contacts.form';
import {DebugElement} from '@angular/core';
import {Contact} from '../../../../../src/model/customer/contact';
import {getMdIconButton} from '../../../../selector-helpers';
import {ApplicationType} from '../../../../../src/model/application/type/application-type';
import {Application} from '../../../../../src/model/application/application';
import {Observable} from 'rxjs/Observable';

const CONTACT1 = new Contact(1, 1, 'contact1', 'address1');
const CONTACT2 = new Contact(2, 1, 'contact2', 'address2');
const CONTACTS_ALL = [CONTACT1, CONTACT2];

describe('ContactComponent', () => {
  let comp: ContactComponent;
  let de: DebugElement;
  let fixture: ComponentFixture<ContactComponent>;
  let page: ContactPage;
  let parentForm: FormGroup;
  let applicationStateMock: ApplicationStateMock;
  let customerHubMock: CustomerHubMock;

  class ContactPage {
    public contacts: Array<DebugElement>;

    constructor() {
      this.update();
    }

    getFromContact(index: number, selector: string) {
      return this.contacts[index].query(By.css(selector));
    }

    update() {
      this.contacts = de.queryAll(By.css('.contact-card'));
    }
  }

  function detectChangesAndUpdate() {
    fixture.detectChanges();
    tick();
    page.update();
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AlluCommonModule, ReactiveFormsModule, MdCardModule],
      declarations: [ContactComponent],
      providers: [
        {provide: ApplicationState, useClass: ApplicationStateMock},
        {provide: FormBuilder, useValue: new FormBuilder()},
        {provide: CustomerHub, useClass: CustomerHubMock}
      ]
    }).compileComponents();

    applicationStateMock = TestBed.get(ApplicationState) as ApplicationStateMock;
    customerHubMock = TestBed.get(CustomerHub) as CustomerHubMock;
  }));

  beforeEach(() => {
    parentForm = createParentForm();
    fixture = TestBed.createComponent(ContactComponent);
    de = fixture.debugElement;
    comp = fixture.componentInstance;
    comp.parentForm = parentForm;
    comp.readonly = false;
    comp.customerRoleType = CustomerRoleType[CustomerRoleType.APPLICANT];
    comp.contactList = CONTACTS_ALL;
    fixture.detectChanges();
    page = new ContactPage();
  });

  it('should show contacts from input', () => {
    fixture.whenStable().then(result => {
      expect(page.contacts.length).toEqual(CONTACTS_ALL.length);
      expect(page.getFromContact(0, '[formControlName="name"]').nativeElement.value).toEqual(CONTACT1.name);
      expect(page.getFromContact(0, '[formControlName="streetAddress"]').nativeElement.value).toEqual(CONTACT1.streetAddress);

      expect(page.getFromContact(1, '[formControlName="name"]').nativeElement.value).toEqual(CONTACT2.name);
      expect(page.getFromContact(1, '[formControlName="streetAddress"]').nativeElement.value).toEqual(CONTACT2.streetAddress);
    });
  });

  it('should show contact info fields disabled', fakeAsync(() => {
    fixture.whenStable().then(result => {
      expect(page.getFromContact(0, '[formControlName="name"] [disabled]')).toBeDefined();
    });
  }));

  it('should add contact when add is called', fakeAsync(() => {
    comp.addContact();
    detectChangesAndUpdate();
    expect(page.contacts.length).toEqual(CONTACTS_ALL.length + 1);
  }));

  it('should remove contact when remove is clicked', fakeAsync(() => {
    const removeBtn = getMdIconButton(page.contacts[0], 'clear');
    removeBtn.click();
    detectChangesAndUpdate();
    expect(page.contacts.length).toEqual(CONTACTS_ALL.length - 1);
  }));

  it('should not allow to remove last contact when contact is required', fakeAsync(() => {
    comp.contactRequired = true;
    getMdIconButton(page.contacts[1], 'clear').click();
    detectChangesAndUpdate();
    expect(page.contacts.length).toEqual(1);
    // Last one can't be removed
    expect(getMdIconButton(page.contacts[0], 'clear')).toBeUndefined();
  }));

  it('should show edit button when application is in edit mode', fakeAsync(() => {
    comp.readonly = false;
    detectChangesAndUpdate();
    const editBtn = getMdIconButton(page.contacts[0], 'mode_edit');
    expect(editBtn).toBeDefined();
  }));

  it('should hide edit button when application is in summary mode', fakeAsync(() => {
    comp.readonly = true;
    detectChangesAndUpdate();
    const editBtn = getMdIconButton(page.contacts[0], 'mode_edit');
    expect(editBtn).toBeUndefined();
  }));

  it('should clear other fields when name is edited', fakeAsync(() => {
    comp.readonly = true;
    detectChangesAndUpdate();
    const inputElement: HTMLInputElement = page.getFromContact(0, '[formControlName="name"]').nativeElement;
    inputElement.value = 'updated value';
    inputElement.dispatchEvent(new Event('keyup'));
    detectChangesAndUpdate();
    expect(page.getFromContact(0, '[formControlName="streetAddress"]').nativeElement.value).toBe('');
  }));

  it('should show orderer for cable report', fakeAsync(() => {
    reInitWithCableReport();
    expect(page.getFromContact(0, '.mat-checkbox')).toBeTruthy();
    expect(page.getFromContact(0, '.mat-checkbox-checked')).toBeFalsy();
  }));

  it('should notify when orderer is selected', fakeAsync(() => {
    const customerHubSpy = spyOn(customerHubMock, 'ordererSelected');
    reInitWithCableReport();
    const checkboxEl = page.getFromContact(0, '.mat-checkbox-label').nativeElement;
    checkboxEl.click();
    detectChangesAndUpdate();
    expect(page.getFromContact(0, '.mat-checkbox-checked').nativeElement).toBeDefined('checkbox was checked');
    expect(customerHubSpy.calls.count()).toEqual(1, 'stubbed method was called');
    const orderer = customerHubSpy.calls.first().args[0];
    expect(orderer.id).toEqual(CONTACT1.id, 'orderer id is correct');
    expect(orderer.orderer).toEqual(true, 'contact was marked as orderer');
  }));

  it('should uncheck orderer when other orderer is selected', fakeAsync(() => {
    reInitWithCableReport();
    comp.contacts.at(0).patchValue({orderer: true});
    detectChangesAndUpdate();
    expect(page.getFromContact(0, '.mat-checkbox-checked').nativeElement).toBeDefined('checkbox was checked');

    CONTACT2.orderer = true;
    customerHubMock.orderer$.next(CONTACT2);
    detectChangesAndUpdate();
    expect(page.getFromContact(0, '.mat-checkbox-checked')).toBeFalsy('checkbox was unchecked');
  }));

  function reInitWithCableReport() {
    const app = new Application();
    app.type = ApplicationType[ApplicationType.CABLE_REPORT];
    spyOnProperty(applicationStateMock, 'application', 'get').and.returnValue(app);
    while (comp.contacts.length) {
      comp.contacts.removeAt(0);
    }
    comp.ngOnDestroy();
    comp.ngOnInit();
    detectChangesAndUpdate();
  }

  function createParentForm() {
    const fb = new FormBuilder();
    const form = fb.group({});
    form.addControl(
      CustomerWithContactsForm.formName(CustomerRoleType.APPLICANT),
      CustomerWithContactsForm.initialForm(fb, CustomerRoleType.APPLICANT));
    return form;
  }
});
