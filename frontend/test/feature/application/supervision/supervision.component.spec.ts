import {Component, DebugElement, EventEmitter, Input, Output} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule} from '@angular/forms';
import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {User} from '@model/user/user';
import {SupervisionComponent} from '@feature/application/supervision/supervision.component';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {availableToDirectiveMockMeta, CurrentUserMock, UserServiceMock} from '../../../mocks';
import {AvailableToDirective} from '@service/authorization/available-to.directive';
import {SupervisionTask} from '@model/application/supervision/supervision-task';
import {findTranslation} from '@util/translations';
import {getButtonWithMatIcon} from '../../../selector-helpers';
import {combineReducers, Store, StoreModule} from '@ngrx/store';
import * as fromSupervisionTask from '@feature/application/supervision/reducers';
import * as fromApplication from '@feature/application/reducers';
import * as ApplicationActions from '@feature/application/actions/application-actions';
import {LoadSuccess} from '@feature/application/supervision/actions/supervision-task-actions';
import {Application} from '@model/application/application';
import {UserService} from '@service/user/user-service';

const firstTask = new SupervisionTask(1);

@Component({
  selector: 'supervision-task',
  template: ''
})
class MockSupervisionTaskComponent {
  @Input() form: FormGroup;
  @Input() supervisors: Array<User> = [];
  @Input() application: Application;
  @Input() hasDisablingTags: boolean;
  @Output() onRemove = new EventEmitter<void>();
}

describe('SupervisionComponent', () => {
  let comp: SupervisionComponent;
  let fixture: ComponentFixture<SupervisionComponent>;
  let store: Store<fromSupervisionTask.State>;
  let de: DebugElement;
  const currentUserMock = CurrentUserMock.create(true, true);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        FormsModule,
        StoreModule.forRoot({
          'supervisionTasks': combineReducers(fromSupervisionTask.reducers),
          'application': combineReducers(fromApplication.reducers)
        })
      ],
      declarations: [
        SupervisionComponent,
        MockSupervisionTaskComponent
      ],
      providers: [
        FormBuilder,
        {provide: UserService, useClass: UserServiceMock}
      ]
    })
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents();
  }));

  beforeEach(() => {
    store = TestBed.get(Store);
    fixture = TestBed.createComponent(SupervisionComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    store.dispatch(new LoadSuccess([firstTask]));
    store.dispatch(new ApplicationActions.LoadSuccess(new Application()));
    comp.ngOnInit();
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
    const btn = getButtonWithMatIcon(de, 'search');
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
