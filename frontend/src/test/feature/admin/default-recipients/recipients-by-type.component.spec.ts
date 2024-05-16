import {DebugElement} from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {UntypedFormBuilder, ReactiveFormsModule} from '@angular/forms';
import {RecipientsByTypeComponent} from '../../../../app/feature/admin/default-recipients/recipients-by-type.component';
import {AlluCommonModule} from '../../../../app/feature/common/allu-common.module';
import {DefaultRecipientHub} from '../../../../app/service/recipients/default-recipient-hub';
import {ApplicationType} from '../../../../app/model/application/type/application-type';
import {DefaultRecipient} from '../../../../app/model/common/default-recipient';
import {RECIPIENT_ONE, RECIPIENT_TWO} from '../../../service/recipients/default-recipient-mock-values';
import {NotificationService} from '../../../../app/feature/notification/notification.service';
import {NotificationServiceMock} from '../../../mocks';
import {share} from 'rxjs/internal/operators';
import {BehaviorSubject, Observable, of} from 'rxjs/index';

class DefaultRecipientHubMock {
  recipients$ = new BehaviorSubject<Array<DefaultRecipient>>([]);

  defaultRecipientsByApplicationType(type: string) {
    return this.recipients$.asObservable().pipe(share());
  }
  removeDefaultRecipient(id: number): Observable<{}> {
    return of({});
  }
  saveDefaultRecipient(recipient: DefaultRecipient): Observable<{}> {
    return of(recipient);
  }
}

describe('RecipientsByTypeComponent', () => {
  let fixture: ComponentFixture<RecipientsByTypeComponent>;
  let comp: RecipientsByTypeComponent;
  let de: DebugElement;
  let hub: DefaultRecipientHubMock;
  let page: Page;

  class Page {
    public static emailDivSelector = By.css('.left-align');
    public static emailInputSelector = By.css('input');

    public rows: Array<DebugElement>;
    public addNewButton: HTMLButtonElement;

    update() {
      this.addNewButton = de.query(By.css('th button')).nativeElement;
      this.rows = de.queryAll(By.css('tr'));
    }

    getButtonFromRow(index: number, buttonIcon: string) {
      return page.rows[index].queryAll(By.css('button'))
        .filter(btn => btn.query(By.css('mat-icon')).nativeElement.textContent === buttonIcon)
        .map(btn => btn.nativeElement)[0];
    }
  }

  function detectChangesAndUpdate() {
    fixture.detectChanges();
    tick();
    page.update();
  }

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [AlluCommonModule, ReactiveFormsModule],
      declarations: [
        RecipientsByTypeComponent
      ],
      providers: [
        UntypedFormBuilder,
        { provide: DefaultRecipientHub, useClass: DefaultRecipientHubMock },
        {provide: NotificationService, useClass: NotificationServiceMock}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RecipientsByTypeComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
    comp.type = ApplicationType[ApplicationType.EVENT];
    hub = TestBed.inject(DefaultRecipientHub) as unknown as DefaultRecipientHubMock;
    hub.recipients$.next([RECIPIENT_ONE, RECIPIENT_TWO]);
    comp.ngOnInit();
    fixture.detectChanges();
    page = new Page();
    page.update();
  });

  it('should show existing default recipients', fakeAsync(() => {
    expect(page.rows.length).toEqual(2, 'Unexpected amount of comments');
    const firstRow = page.rows[0];
    expect(firstRow.query(Page.emailDivSelector).nativeElement.textContent).toEqual(RECIPIENT_ONE.email);
  }));

  it('should add new on button press', fakeAsync(() => {
    page.addNewButton.click();
    detectChangesAndUpdate();
    expect(page.rows.length).toEqual(3, 'Did not add a new row');
    expect(de.queryAll(Page.emailDivSelector).length).toBe(2, 'Existing rows with value not found');
    expect(de.queryAll(Page.emailInputSelector).length).toBe(1, 'Input not found');
    expect(page.rows[2].query(Page.emailInputSelector).nativeElement.value).toBeFalsy('Email input should not have value');
    comp.onItemCountChanged.subscribe(count => expect(count).toEqual(3, 'Component did not notify count by output'));
  }));

  it('should delete row on delete button click', fakeAsync(() => {
    spyOn(hub, 'removeDefaultRecipient').and.returnValue(of({}));
    const deleteBtn = page.getButtonFromRow(0, 'clear');
    deleteBtn.click();
    detectChangesAndUpdate();
    expect(hub.removeDefaultRecipient).toHaveBeenCalledTimes(1);
    comp.onItemCountChanged.subscribe(count => expect(count).toEqual(2, 'Component did not notify count after removal'));
  }));

  it('should toggle edit mode on edit button click', fakeAsync(() => {
    const editBtn = page.getButtonFromRow(0, 'edit');
    editBtn.click();
    detectChangesAndUpdate();
    expect(de.queryAll(Page.emailInputSelector).length).toBe(1, 'Input not found');
  }));

  it('should save item on save button click', fakeAsync(() => {
    const updated = new DefaultRecipient(RECIPIENT_ONE.id, 'updated@email.fi', RECIPIENT_ONE.applicationType);
    spyOn(hub, 'saveDefaultRecipient').and.returnValue(of(updated));
    const editBtn = page.getButtonFromRow(0, 'edit');
    editBtn.click();
    detectChangesAndUpdate();

    const firstRow = page.rows[0];
    const inputElement: HTMLInputElement = firstRow.query(Page.emailInputSelector).nativeElement;
    inputElement.value = updated.email;
    inputElement.dispatchEvent(new Event('input'));
    detectChangesAndUpdate();

    const saveButton = page.getButtonFromRow(0, 'save');
    saveButton.click();
    expect(hub.saveDefaultRecipient).toHaveBeenCalledWith(updated);
  }));

  it('save button should be disabled on invalid email', fakeAsync(() => {
    const editBtn = page.getButtonFromRow(0, 'edit');
    editBtn.click();
    detectChangesAndUpdate();

    const firstRow = page.rows[0];
    const inputElement: HTMLInputElement = firstRow.query(Page.emailInputSelector).nativeElement;
    inputElement.value = 'invalidEmail';
    inputElement.dispatchEvent(new Event('input'));
    detectChangesAndUpdate();

    const saveButton = page.getButtonFromRow(0, 'save');
    expect(saveButton.disabled).toBeTruthy();
  }));
});

