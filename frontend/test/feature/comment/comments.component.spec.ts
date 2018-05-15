import {Component, DebugElement, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule} from '@angular/forms';
import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {AlluCommonModule} from '../../../src/app/feature/common/allu-common.module';
import {CommentsComponent} from '../../../src/app/feature/comment/comments.component';
import {ApplicationStore} from '../../../src/app/service/application/application-store';
import {CommentType} from '../../../src/app/model/application/comment/comment-type';
import {Comment} from '../../../src/app/model/application/comment/comment';
import {NotificationService} from '../../../src/app/service/notification/notification.service';
import {ApplicationStoreMock, availableToDirectiveMockMeta, CurrentUserMock, NotificationServiceMock} from '../../mocks';
import {AvailableToDirective} from '../../../src/app/service/authorization/available-to.directive';
import {Subject} from 'rxjs/Subject';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {CommentComponent} from '../../../src/app/feature/comment/comment.component';


const COMMENT_ONE = new Comment(
  1,
  CommentType[CommentType.INTERNAL],
  'Test comment one',
  new Date()
);

const COMMENT_TWO = new Comment(
  2,
  CommentType[CommentType.INVOICING],
  'Test comment two',
  new Date()
);

@Component({
  selector: 'parent',
  template: `<comments [comments]="comments$.asObservable() | async"
                       (save)="save($event)"
                       (remove)="remove($event)"></comments>`
})
class MockParentComponent {
  comments$ = new BehaviorSubject<Comment[]>([]);

  @ViewChild(CommentsComponent) commentsComponent: CommentsComponent;

  save(comment: Comment): void {}
  remove(comment: Comment): void {}
}

@Component({
  selector: 'comment',
  template: ''
})
class MockCommentComponent {
  @Input() comment: Comment = new Comment();
  @Output('save') onSave = new EventEmitter<Comment>();
  @Output('remove') onRemove = new EventEmitter<Comment>();

  form = new FormGroup({});
}

describe('CommentsComponent', () => {
  let parentComp: MockParentComponent;
  let fixture: ComponentFixture<MockParentComponent>;
  let comp: CommentsComponent;
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AlluCommonModule, FormsModule],
      declarations: [
        MockParentComponent,
        CommentsComponent,
        MockCommentComponent
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MockParentComponent);
    parentComp = fixture.componentInstance;
    comp = parentComp.commentsComponent;
    de = fixture.debugElement;

    parentComp.comments$.next([COMMENT_ONE, COMMENT_TWO]);
    fixture.detectChanges();
  });

  it('should show comments', () => {
    parentComp.comments$.next([COMMENT_ONE, COMMENT_TWO]);
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(de.queryAll(By.css('li')).length).toEqual(2, 'Unexpected amount of comments');
    });
  });

  it('should re-emit save', fakeAsync(() => {
    spyOn(parentComp, 'save');
    const commentComp = comp.children.first;
    const comment = new Comment(1);
    commentComp.onSave.emit(new Comment(1));
    expect(parentComp.save).toHaveBeenCalledWith(comment);
  }));

  it('should re-emit remove', () => {
    spyOn(parentComp, 'remove');
    const commentComp = comp.children.first;
    const comment = new Comment(1);
    commentComp.onRemove.emit(new Comment(1));
    expect(parentComp.remove).toHaveBeenCalledWith(comment);
  });

  it('should tell if any of the comments are marked as dirty', () => {
    const commentComp = comp.children.first;
    commentComp.form.markAsDirty();
    expect(comp.dirty).toEqual(true);
  });

  /*
  it('should show header', () => {
    const title = fixture.debugElement.query(By.css('h1.content-header')).nativeElement;
    expect(title.textContent).toEqual('Kommentit');
  });

  it('should handle comment loading errors', fakeAsync(() => {
    spyOn(notification, 'errorInfo');
    applicationStore.comments$.error(new ErrorInfo('testMessage'));
    expect(notification.errorInfo).toHaveBeenCalled();
  }));

  it('should handle adding new comment', fakeAsync(() => {
    const btn = de.query(By.css('button')).nativeElement;
    btn.click();
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(de.queryAll(By.css('li')).length).toEqual(3, 'Was expecting 2 + 1 comments');
    });
  }));

  it('should save comment', fakeAsync(() => {
    spyOn(applicationStore, 'saveComment').and.returnValue(Observable.of(COMMENT_ONE));
    spyOn(notification, 'success');

    fixture.detectChanges();
    fixture.whenStable().then(val => {
      const commentEl = de.query(By.css('comment'));
      commentEl.triggerEventHandler('onSave', COMMENT_ONE);
      expect(applicationStore.saveComment).toHaveBeenCalledWith(applicationStore.snapshot.application.id, COMMENT_ONE);
      expect(notification.success).toHaveBeenCalled();
      expect(de.queryAll(By.css('li')).length).toEqual(2, 'Was expecting 2 comments');
    });
  }));

  it('should handle save comment error', fakeAsync(() => {
    spyOn(applicationStore, 'saveComment').and.returnValue(Observable.throw(new ErrorInfo('Error!')));
    spyOn(notification, 'error');
    comp.save(COMMENT_ONE);
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(notification.error).toHaveBeenCalled();
    });
  }));

  it('should remove comment', fakeAsync(() => {
    spyOn(applicationStore, 'removeComment').and.returnValue(Observable.of({}));
    spyOn(notification, 'success');
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      const commentEl = de.query(By.css('comment'));
      commentEl.triggerEventHandler('onRemove', COMMENT_ONE);
      expect(applicationStore.removeComment).toHaveBeenCalledWith(COMMENT_ONE.id);
      expect(notification.success).toHaveBeenCalledTimes(1);
    });

    tick();
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(de.queryAll(By.css('li')).length).toEqual(1, 'Was expecting 1 comment');
    });
  }));

  it('should handle remove comment error', fakeAsync(() => {
    spyOn(applicationStore, 'removeComment').and.returnValue(Observable.throw(new ErrorInfo('Error!')));
    spyOn(notification, 'error');
    comp.remove(COMMENT_ONE);
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(notification.error).toHaveBeenCalled();
      expect(de.queryAll(By.css('li')).length).toEqual(2, 'Was expecting 2 comments');
    });
  }));*/
});
