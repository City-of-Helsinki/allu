import {Component, DebugElement, EventEmitter, Input, Output} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule} from '@angular/forms';
import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {User} from '../../../../src/app/model/user/user';
import {SupervisionComponent} from '../../../../src/app/feature/application/supervision/supervision.component';
import {SupervisionTaskStore} from '../../../../src/app/service/supervision/supervision-task-store';
import {UserHub} from '../../../../src/app/service/user/user-hub';
import {AlluCommonModule} from '../../../../src/app/feature/common/allu-common.module';
import {availableToDirectiveMockMeta, CurrentUserMock, UserHubMock} from '../../../mocks';
import {AvailableToDirective} from '../../../../src/app/service/authorization/available-to.directive';
import {SupervisionTask} from '../../../../src/app/model/application/supervision/supervision-task';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {findTranslation} from '../../../../src/app/util/translations';
import {getMdIconButton} from '../../../selector-helpers';

const firstTask = new SupervisionTask(1);

@Component({
  selector: 'supervision-task',
  template: ''
})
class SupervisionTaskComponentMock {
  @Input() form: FormGroup;
  @Input() supervisors: Array<User> = [];
  @Output() onRemove = new EventEmitter<void>();
}

class SupervisionTaskStoreMock {
  public tasks$ = new Subject<Array<SupervisionTask>>();
  get tasks(): Observable<Array<SupervisionTask>> {
    return this.tasks$.asObservable();
  }
}

describe('SupervisionComponent', () => {
  let comp: SupervisionComponent;
  let fixture: ComponentFixture<SupervisionComponent>;
  let supervisionTaskStore: SupervisionTaskStoreMock;
  let de: DebugElement;
  let currentUserMock = CurrentUserMock.create(true, true);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AlluCommonModule, FormsModule],
      declarations: [
        SupervisionComponent,
        SupervisionTaskComponentMock
      ],
      providers: [
        FormBuilder,
        {provide: SupervisionTaskStore, useClass: SupervisionTaskStoreMock},
        {provide: UserHub, useClass: UserHubMock}
      ]
    })
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents();
  }));

  beforeEach(() => {
    supervisionTaskStore = TestBed.get(SupervisionTaskStore) as SupervisionTaskStoreMock;
    fixture = TestBed.createComponent(SupervisionComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    comp.ngOnInit();
    supervisionTaskStore.tasks$.next([firstTask]);
    fixture.detectChanges();
  });

  afterEach(() => {
    comp.ngOnDestroy();
  });

  it('should show header', () => {
    const title = de.query(By.css('h1.content-header')).nativeElement;
    expect(title.textContent).toEqual(findTranslation('supervision.title'));
  });

  it('should show list of tasks', () => {
    const tasks = de.queryAll(By.css('li'));
    expect(tasks.length).toEqual(1);
  });

  it('should add new tas to the list when pressing button', fakeAsync(() => {
    const btn = getMdIconButton(de, 'search');
    btn.click();
    fixture.detectChanges();
    tick();
    const tasks = de.queryAll(By.css('li'));
    expect(tasks.length).toEqual(2);
  }));

  it('should remove item when onRemove is emitted', fakeAsync(() => {
    const firstTaskElem = <HTMLElement>de.query(By.css('supervision-task')).nativeElement;
    firstTaskElem.dispatchEvent(new Event('onRemove'));
    fixture.detectChanges();
    tick();
    const tasks = de.queryAll(By.css('li'));
    expect(tasks.length).toEqual(0);
  }));
});
