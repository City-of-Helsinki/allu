import {Component} from '@angular/core';
import {TestBed, async} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {CurrentUser} from '../../../src/service/user/current-user';
import {AvailableToDirective} from '../../../src/service/authorization/available-to.directive';
import {User} from '../../../src/model/common/user';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'test-component',
  template: ''
})
class TestComponent {}

const currentUser = new User(
  1,
  'username',
  'realname',
  'email',
  'title',
  true,
  undefined,
  ['ALLOWED_TYPE'],
  ['ROLE_TEST'],
  []
);

const AVAILABLE = 'AVAILABLE';

class CurrentUserMock {
  get user(): Observable<User> {
    return Observable.of(currentUser);
  }
}

describe('AvailableToDirective', () => {

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TestComponent, AvailableToDirective],
      providers: [
        AvailableToDirective,
        { provide: CurrentUser, useClass: CurrentUserMock}
      ]
    });
  }));

  it('Element should be available when user has required role', () => {
    const div = createDivWithDirective(currentUser.roles[0]);

    TestBed.overrideTemplate(TestComponent, div).compileComponents().then(res => {
      expectElement(element => {
        expect(element).not.toBeNull('Element was not available');
        expect(element.nativeElement.textContent).toContain(AVAILABLE, 'AVAILABLE text was not available');
      });
    });
  });

  it('Element should not be available when user has required role', () => {
    const div = createDivWithDirective('ROLE_MISSING');

    TestBed.overrideTemplate(TestComponent, div).compileComponents().then(res => {
      expectElement(element => {
        expect(element).toBeNull('Element was available when it should not');
      });
    });
  });

  it('Element should be available when user has required role and application type', () => {
    const div = createDivWithDirective(currentUser.roles[0], currentUser.allowedApplicationTypes[0]);

    TestBed.overrideTemplate(TestComponent, div).compileComponents().then(res => {
      expectElement(element => {
        expect(element).not.toBeNull('Element was not available');
        expect(element.nativeElement.textContent).toContain(AVAILABLE, 'AVAILABLE text was not available');
      });
    });
  });

  it('Element should not be available when user has required role and wrong application type', () => {
    const div = createDivWithDirective(currentUser.roles[0], 'TYPE_WRONG');

    TestBed.overrideTemplate(TestComponent, div).compileComponents().then(res => {
      expectElement(element => {
        expect(element).toBeNull('Element was available when it should not');
      });
    });
  });

  it('Directive should throw when no roles are given', () => {
    const div = '<div *availableTo id="content">AVAILABLE</div>';
    TestBed.overrideTemplate(TestComponent, div).compileComponents()
      .then(noError => {
        const fixture = TestBed.createComponent(TestComponent);
        fixture.detectChanges();
        fail('No error thrown');
      })
      .catch(err => expect(err.message).toContain('Available to requires at least one role as parameter'));
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
