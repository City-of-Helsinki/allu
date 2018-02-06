import {Component, DebugElement, EventEmitter, Input, Output} from '@angular/core';
import {FormArray, FormGroup, FormBuilder, FormsModule} from '@angular/forms';
import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {Observable} from 'rxjs/Observable';
import {AlluCommonModule} from '../../../../src/app/feature/common/allu-common.module';
import {CommentsComponent} from '../../../../src/app/feature/application/comment/comments.component';
import {CommentForm} from '../../../../src/app/feature/application/comment/comment-form';
import {ApplicationStore} from '../../../../src/app/service/application/application-store';
import {CommentType} from '../../../../src/app/model/application/comment/comment-type';
import {Comment} from '../../../../src/app/model/application/comment/comment';
import {ErrorInfo} from '../../../../src/app/service/ui-state/error-info';
import {NotificationService} from '../../../../src/app/service/notification/notification.service';
import {HttpResponse, HttpStatus} from '../../../../src/app/util/http-response';
import {ApplicationStoreMock, availableToDirectiveMockMeta, CurrentUserMock} from '../../../mocks';
import {AvailableToDirective} from '../../../../src/app/service/authorization/available-to.directive';


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
  selector: 'comment',
  template: ''
})
class MockCommentComponent {
  @Input() form: FormGroup;
  @Output() onRemove = new EventEmitter<Comment>();
  @Output() onSave = new EventEmitter<Comment>();
}

describe('CommentsComponent', () => {
  let comp: CommentsComponent;
  let fixture: ComponentFixture<CommentsComponent>;
  let applicationStore: ApplicationStoreMock;
  let de: DebugElement;
  const currentUserMock = CurrentUserMock.create(true, true);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AlluCommonModule, FormsModule],
      declarations: [
        CommentsComponent,
        MockCommentComponent
      ],
      providers: [
        FormBuilder,
        { provide: ApplicationStore, useClass: ApplicationStoreMock }
      ]
    })
    .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
    .compileComponents();
  }));

  beforeEach(() => {
    applicationStore = TestBed.get(ApplicationStore) as ApplicationStoreMock;
    fixture = TestBed.createComponent(CommentsComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    comp.ngOnInit();
    applicationStore.comments$.next([COMMENT_ONE, COMMENT_TWO]);
    fixture.detectChanges();
  });

  afterEach(() => {
    comp.ngOnDestroy();
  });

  it('should show header', () => {
    const title = fixture.debugElement.query(By.css('h1.content-header')).nativeElement;
    expect(title.textContent).toEqual('Kommentit');
  });

  it('should show comments', fakeAsync(() => {
    applicationStore.comments$.next([COMMENT_ONE, COMMENT_TWO]);
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(de.queryAll(By.css('li')).length).toEqual(2, 'Unexpected amount of comments');
    });
  }));

  it('should handle comment loading errors', fakeAsync(() => {
    spyOn(NotificationService, 'error');
    applicationStore.comments$.error(ErrorInfo.of(undefined, 'testMessage'));
    expect(NotificationService.error).toHaveBeenCalled();
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
    spyOn(NotificationService, 'message');

    fixture.detectChanges();
    fixture.whenStable().then(val => {
      const commentEl = de.query(By.css('comment'));
      commentEl.triggerEventHandler('onSave', COMMENT_ONE);
      expect(applicationStore.saveComment).toHaveBeenCalledWith(applicationStore.snapshot.application.id, COMMENT_ONE);
      expect(NotificationService.message).toHaveBeenCalled();
      expect(de.queryAll(By.css('li')).length).toEqual(2, 'Was expecting 2 comments');
    });
  }));

  it('should handle save comment error', fakeAsync(() => {
    spyOn(applicationStore, 'saveComment').and.returnValue(Observable.throw(ErrorInfo.of(undefined, 'Error!')));
    spyOn(NotificationService, 'errorMessage');
    comp.save(0, COMMENT_ONE);
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(NotificationService.errorMessage).toHaveBeenCalled();
    });
  }));

  it('should remove comment', fakeAsync(() => {
    spyOn(applicationStore, 'removeComment').and.returnValue(Observable.of(new HttpResponse(HttpStatus.OK, 'Good job')));
    spyOn(NotificationService, 'message');
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      const commentEl = de.query(By.css('comment'));
      commentEl.triggerEventHandler('onRemove', COMMENT_ONE);
      expect(applicationStore.removeComment).toHaveBeenCalledWith(COMMENT_ONE.id);
      expect(NotificationService.message).toHaveBeenCalledTimes(1);
    });

    tick();
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(de.queryAll(By.css('li')).length).toEqual(1, 'Was expecting 1 comment');
    });
  }));

  it('should handle remove comment error', fakeAsync(() => {
    spyOn(applicationStore, 'removeComment').and.returnValue(Observable.throw(ErrorInfo.of(undefined, 'Error!')));
    spyOn(NotificationService, 'errorMessage');
    comp.remove(0, COMMENT_ONE);
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(NotificationService.errorMessage).toHaveBeenCalled();
      expect(de.queryAll(By.css('li')).length).toEqual(2, 'Was expecting 2 comments');
    });
  }));
});
