import {Component, DebugElement, ViewChild} from '@angular/core';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {CommentComponent} from '@feature/comment/comment.component';
import {Comment} from '@model/application/comment/comment';
import {User} from '@model/user/user';
import {By} from '@angular/platform-browser';
import {AvailableToDirective} from '@service/authorization/available-to.directive';
import {availableToDirectiveMockMeta, CurrentUserMock} from '../../mocks';
import {CurrentUser} from '@service/user/current-user';
import {CommentType} from '@model/application/comment/comment-type';

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
  const currentUser: CurrentUserMock = CurrentUserMock.create(true, true);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        FormsModule,
        ReactiveFormsModule,
      ],
      declarations: [
        MockParentComponent,
        CommentComponent
      ],
      providers: [
        FormBuilder,
        {provide: CurrentUser, useValue: currentUser}
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

    user = new User(1, 'testUser');
    parentComp.comment = new Comment(1, CommentType.INTERNAL, 'some text', new Date(), new Date(), user);
    fixture.detectChanges();
  });

  it('should show relevant content for saved comment', () => {
    expect(de.query(By.css('.save-info'))).toBeDefined();
    expect(de.query(By.css('[formControlName="type"]'))).toBeNull();
  });

  it('should show relevant content for new comment', () => {
    parentComp.comment = new Comment(undefined, CommentType.INTERNAL, 'some text');
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      expect(de.query(By.css('[formControlName="type"]'))).toBeDefined();
      expect(de.query(By.css('.save-info'))).toBeNull();
    });
  });
});
