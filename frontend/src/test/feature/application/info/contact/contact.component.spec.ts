import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {UntypedFormBuilder, UntypedFormGroup, ReactiveFormsModule} from '@angular/forms';
import {MatLegacyCardModule as MatCardModule} from '@angular/material/legacy-card';
import {ContactComponent} from '@feature/application/info/contact/contact.component';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {ApplicationStore} from '@service/application/application-store';
import {ApplicationStoreMock, ContactServiceMock, CustomerServiceMock, NotificationServiceMock} from 'test/mocks';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {CustomerWithContactsForm} from '@feature/customerregistry/customer/customer-with-contacts.form';
import {DebugElement} from '@angular/core';
import {Contact} from '@model/customer/contact';
import {getMatIconButton} from 'test/selector-helpers';
import {ApplicationType} from '@model/application/type/application-type';
import {Application} from '@model/application/application';
import {CustomerWithContacts} from '@model/customer/customer-with-contacts';
import {CustomerService} from '@service/customer/customer.service';
import {NotificationService} from '@feature/notification/notification.service';
import {ContactService} from '@service/customer/contact.service';
import {StoreModule} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

const CONTACT1 = new Contact(1, 1, 'contact1', 'address1');
const CONTACT2 = new Contact(2, 1, 'contact2', 'address2');
const CONTACTS_ALL = [CONTACT1, CONTACT2];

describe('ContactComponent', () => {
  let comp: ContactComponent;
  let de: DebugElement;
  let fixture: ComponentFixture<ContactComponent>;
  let page: ContactPage;
  let parentForm: UntypedFormGroup;
  let applicationStore: ApplicationStoreMock;

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
  }

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        ReactiveFormsModule,
        MatCardModule,
        StoreModule.forRoot(fromRoot.reducers),
        NoopAnimationsModule
      ],
      declarations: [ContactComponent],
      providers: [
        {provide: ApplicationStore, useClass: ApplicationStoreMock},
        {provide: UntypedFormBuilder, useValue: new UntypedFormBuilder()},
        {provide: CustomerService, useClass: CustomerServiceMock},
        {provide: ContactService, useClass: ContactServiceMock},
        {provide: NotificationService, useClass: NotificationServiceMock},
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    parentForm = createParentForm();
    fixture = TestBed.createComponent(ContactComponent);
    de = fixture.debugElement;
    comp = fixture.componentInstance;
    comp.parentForm = parentForm;
    comp.readonly = false;
    comp.customerRoleType = CustomerRoleType[CustomerRoleType.APPLICANT];

    applicationStore = TestBed.inject(ApplicationStore) as unknown as ApplicationStoreMock;
    const app = applicationStore.snapshot.application;
    app.customersWithContacts = [
      new CustomerWithContacts(CustomerRoleType.APPLICANT, undefined, CONTACTS_ALL)
    ];
    applicationStore.applicationChange(app);

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
    const removeBtn = getMatIconButton(page.contacts[0], 'clear');
    removeBtn.click();
    detectChangesAndUpdate();
    expect(page.contacts.length).toEqual(CONTACTS_ALL.length - 1);
  }));

  it('should not allow to remove last contact when contact is required', fakeAsync(() => {
    comp.contactRequired = true;
    getMatIconButton(page.contacts[1], 'clear').click();
    detectChangesAndUpdate();
    expect(page.contacts.length).toEqual(1);
    // Last one can't be removed
    expect(getMatIconButton(page.contacts[0], 'clear')).toBeUndefined();
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

  it('should uncheck orderer when other orderer is selected', fakeAsync(() => {
    reInitWithCableReport();
    page.getFromContact(1, '.mat-radio-label').nativeElement.click();
    detectChangesAndUpdate();
    expect(page.getFromContact(0, '.mat-radio-button').componentInstance.checked).toBe(false, 'original checkbox was checked');
    expect(page.getFromContact(1, '.mat-radio-button').componentInstance.checked).toBe(true, 'clicked checkbox was unchecked');
  }));

  function reInitWithCableReport() {
    const app = new Application();
    app.type = ApplicationType[ApplicationType.CABLE_REPORT];
    spyOnProperty(applicationStore, 'snapshot', 'get').and.returnValue({application: app});
    while (comp.contacts.length) {
      comp.contacts.removeAt(0);
    }

    comp.ngOnInit();
    detectChangesAndUpdate();
  }

  function createParentForm() {
    const fb = new UntypedFormBuilder();
    const form = fb.group({
      ordererId: [undefined]
    });
    form.addControl(
      CustomerWithContactsForm.formName(CustomerRoleType.APPLICANT),
      CustomerWithContactsForm.initialForm(fb, CustomerRoleType.APPLICANT));
    return form;
  }
});
