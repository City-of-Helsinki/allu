import {Component, DebugElement, ViewChild} from '@angular/core';
import {AlluCommonModule} from '../../../src/app/feature/common/allu-common.module';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';
import {CommentComponent} from '../../../src/app/feature/comment/comment.component';
import {Comment} from '../../../src/app/model/application/comment/comment';
import {User} from '../../../src/app/model/user/user';
import {combineReducers, Store, StoreModule} from '@ngrx/store';
import * as fromAuth from '../../../src/app/feature/auth/reducers';
import {LoggedUserLoaded} from '../../../src/app/feature/auth/actions/auth-actions';
import {By} from '@angular/platform-browser';
import {AvailableToDirective} from '../../../src/app/service/authorization/available-to.directive';
import {availableToDirectiveMockMeta} from '../../mocks';

@Component({
  selector: 'parent',
  template: `<comment [comment]="comment"
                      (save)="save($event)"
                      (remove)="remove($event)"></comment>`
})
class MockParentComponent {
  comment: Comment;

  @ViewChild(CommentComponent) commentComponent: CommentComponent;

  save(comment: Comment): void {}
  remove(comment: Comment): void {}
}

describe('CommentComponent', () => {
  let parentComp: MockParentComponent;
  let fixture: ComponentFixture<MockParentComponent>;
  let comp: CommentComponent;
  let de: DebugElement;
  let user: User;
  let store: Store<fromAuth.State>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        FormsModule,
        ReactiveFormsModule,
        StoreModule.forRoot({
          'auth': combineReducers(fromAuth.reducers)
        }),
      ],
      declarations: [
        MockParentComponent,
        CommentComponent
      ],
      providers: [
        FormBuilder
      ]
    })
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta())
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MockParentComponent);
    parentComp = fixture.componentInstance;
    comp = parentComp.commentComponent;
    de = fixture.debugElement;
    store = TestBed.get(Store);

    user = new User(1, 'testUser');
    store.dispatch(new LoggedUserLoaded(user));
    parentComp.comment = new Comment(1, 'INTERNAL', 'some text', new Date(), new Date(), user);
    fixture.detectChanges();
  });

  it('should show relevant content for saved comment', () => {
    expect(de.query(By.css('.save-info'))).toBeDefined();
    expect(de.query(By.css('[formControlName="type"]'))).toBeNull();
  });

  it('should show relevant content for new comment', () => {
    parentComp.comment = new Comment(undefined, 'INTERNAL', 'some text');
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      expect(de.query(By.css('[formControlName="type"]'))).toBeDefined();
      expect(de.query(By.css('.save-info'))).toBeNull();
    });
  });
});
