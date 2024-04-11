import {Component} from '@angular/core';
import { TestBed, waitForAsync } from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {CurrentUser} from '../../../src/app/service/user/current-user';
import {AvailableToDirective} from '../../../src/app/service/authorization/available-to.directive';
import {availableToDirectiveMockMeta, CurrentUserMock} from '../../mocks';

@Component({
  selector: 'test-component',
  template: ''
})
class TestComponent {}

const AVAILABLE = 'AVAILABLE';

describe('AvailableToDirective', () => {
  const currentUserMock = CurrentUserMock.create(true, true);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [TestComponent, AvailableToDirective],
      providers: [
        AvailableToDirective,
        { provide: CurrentUser, useValue: currentUserMock}
      ]
    });
  }));

  it('Element should be available when user has required role', () => {
    const div = createDivWithDirective('ROLE_OK');
    currentUserMock.allowHasRole = true;
    currentUserMock.allowHasType = false;

    TestBed.overrideTemplate(TestComponent, div)
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents().then(res => {
      expectElement(element => {
        expect(element).not.toBeNull('Element was not available');
        expect(element.nativeElement.textContent).toContain(AVAILABLE, 'AVAILABLE text was not available');
      });
    });
  });

  it('Element should not be available when user has no required role', () => {
    const div = createDivWithDirective('ROLE_WRONG');
    currentUserMock.allowHasRole = false;

    TestBed.overrideTemplate(TestComponent, div)
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents().then(res => {
      expectElement(element => {
        expect(element).toBeNull('Element was available when it should not');
      });
    });
  });

  it('Element should be available when user has required role and application type', () => {
    const div = createDivWithDirective('ROLE_OK', 'TYPE_OK');
    currentUserMock.allowHasRole = true;
    currentUserMock.allowHasType = true;

    TestBed.overrideTemplate(TestComponent, div)
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents().then(res => {
      expectElement(element => {
        expect(element).not.toBeNull('Element was not available');
        expect(element.nativeElement.textContent).toContain(AVAILABLE, 'AVAILABLE text was not available');
      });
    });
  });

  it('Element should not be available when user has required role and wrong application type', () => {
    const div = createDivWithDirective('ROLE_OK', 'TYPE_WRONG');
    currentUserMock.allowHasRole = true;
    currentUserMock.allowHasType = false;

    TestBed.overrideTemplate(TestComponent, div)
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents().then(res => {
      expectElement(element => {
        expect(element).toBeNull('Element was available when it should not');
      });
    });
  });

});

function expectElement(expectFn: (ele) => void) {
  const fixture = TestBed.createComponent(TestComponent);
  fixture.detectChanges();
  fixture.whenStable().then(fx => {
    const element = fixture.debugElement.query(By.css('#content'));
    expectFn.call(this, element);
  });
}

function createDivWithDirective(role: string, type?: string): string {
  if (type) {
    return '<div *availableTo="[\'ROLE\']; types [\'TYPE\']" id="content">AVAILABLE</div>'
      .replace('ROLE', role)
      .replace('TYPE', type);
  } else {
    return '<div *availableTo="[\'ROLE\']" id="content">AVAILABLE</div>'
      .replace('ROLE', role);
  }
}
