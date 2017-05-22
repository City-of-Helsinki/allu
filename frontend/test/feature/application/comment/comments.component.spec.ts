import {Component, DebugElement, Input, Output, EventEmitter} from '@angular/core';
import {ComponentFixture, TestBed, async, fakeAsync, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {Observable} from 'rxjs/Observable';
import {FormsModule} from '@angular/forms';
import {AlluCommonModule} from '../../../../src/feature/common/allu-common.module';
import {CommentsComponent} from '../../../../src/feature/application/comment/comments.component';
import {ApplicationState} from '../../../../src/service/application/application-state';
import {CommentType} from '../../../../src/model/application/comment/comment-type';
import {Comment} from '../../../../src/model/application/comment/comment';
import {Subject} from 'rxjs/Subject';
import {ErrorInfo} from '../../../../src/service/ui-state/error-info';
import {NotificationService} from '../../../../src/service/notification/notification.service';
import {HttpStatus, HttpResponse} from '../../../../src/util/http-response';


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

class ApplicationStateMock {
  public comments$ = new Subject<Array<Comment>>();
  get application() { return {id: 1}; };
  get comments() { return this.comments$.asObservable(); }
  saveComment(applicationId: number, comment: Comment) {}
  removeComment(comment: Comment) {}
}

@Component({
  selector: 'comment',
  template: ''
})
class CommentComponentMock {
  @Input() comment = new Comment();
  @Output() onRemove = new EventEmitter<Comment>();
  @Output() onSave = new EventEmitter<Comment>();
}

describe('CommentsComponent', () => {
  let comp: CommentsComponent;
  let fixture: ComponentFixture<CommentsComponent>;
  let applicationState: ApplicationStateMock;
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AlluCommonModule, FormsModule],
      declarations: [
        CommentsComponent,
        CommentComponentMock
      ],
      providers: [
        { provide: ApplicationState, useClass: ApplicationStateMock }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    applicationState = TestBed.get(ApplicationState) as ApplicationStateMock;
    fixture = TestBed.createComponent(CommentsComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    comp.ngOnInit();
    comp.comments = [COMMENT_ONE, COMMENT_TWO];
  });

  afterEach(() => {
    comp.ngOnDestroy();
  });

  it('should show header', () => {
    let title = fixture.debugElement.query(By.css('h1.content-header')).nativeElement;
    expect(title.textContent).toEqual('Kommentit');
  });

  it('should show comments', fakeAsync(() => {
    applicationState.comments$.next([COMMENT_ONE, COMMENT_TWO]);
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(de.queryAll(By.css('li')).length).toEqual(2, 'Unexpected amount of comments');
    });
  }));

  it('should handle comment loading errors', fakeAsync(() => {
    spyOn(NotificationService, 'error');
    applicationState.comments$.error(ErrorInfo.of(undefined, 'testMessage'));
    expect(NotificationService.error).toHaveBeenCalled();
  }));

  it('should handle adding new comment', fakeAsync(() => {
    let btn = de.query(By.css('button')).nativeElement;
    btn.click();
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(de.queryAll(By.css('li')).length).toEqual(3, 'Was expecting 2 + 1 comments');
    });
  }));

  it('should save comment', fakeAsync(() => {
    spyOn(applicationState, 'saveComment').and.returnValue(Observable.of(COMMENT_ONE));
    spyOn(NotificationService, 'message');

    fixture.detectChanges();
    fixture.whenStable().then(val => {
      let commentEl = de.query(By.css('comment'));
      commentEl.triggerEventHandler('onSave', COMMENT_ONE);
      expect(applicationState.saveComment).toHaveBeenCalledWith(applicationState.application.id, COMMENT_ONE);
      expect(NotificationService.message).toHaveBeenCalled();
      expect(de.queryAll(By.css('li')).length).toEqual(2, 'Was expecting 2 comments');
    });
  }));

  it('should handle save comment error', fakeAsync(() => {
    spyOn(applicationState, 'saveComment').and.returnValue(Observable.throw(ErrorInfo.of(undefined, 'Error!')));
    spyOn(NotificationService, 'errorMessage');
    comp.save(0, COMMENT_ONE);
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(NotificationService.errorMessage).toHaveBeenCalled();
    });
  }));

  it('should remove comment', fakeAsync(() => {
    spyOn(applicationState, 'removeComment').and.returnValue(Observable.of(new HttpResponse(HttpStatus.OK, 'Good job')));
    spyOn(NotificationService, 'message');
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      let commentEl = de.query(By.css('comment'));
      commentEl.triggerEventHandler('onRemove', COMMENT_ONE);
      expect(applicationState.removeComment).toHaveBeenCalledWith(COMMENT_ONE);
      expect(NotificationService.message).toHaveBeenCalledTimes(1);
    });

    tick();
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(de.queryAll(By.css('li')).length).toEqual(1, 'Was expecting 1 comment');
    });
  }));

  it('should handle remove comment error', fakeAsync(() => {
    spyOn(applicationState, 'removeComment').and.returnValue(Observable.throw(ErrorInfo.of(undefined, 'Error!')));
    spyOn(NotificationService, 'errorMessage');
    comp.remove(0, COMMENT_ONE);
    fixture.detectChanges();
    fixture.whenStable().then(val => {
      expect(NotificationService.errorMessage).toHaveBeenCalled();
      expect(de.queryAll(By.css('li')).length).toEqual(2, 'Was expecting 2 comments');
    });
  }));
});
