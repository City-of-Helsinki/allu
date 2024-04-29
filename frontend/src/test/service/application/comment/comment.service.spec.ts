import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {CommentService} from '../../../../app/service/application/comment/comment.service';
import {Comment} from '../../../../app/model/application/comment/comment';
import {CommentType} from '../../../../app/model/application/comment/comment-type';
import {ErrorHandler} from '../../../../app/service/error/error-handler.service';
import {HttpClient} from '@angular/common/http';
import {ActionTargetType} from '../../../../app/feature/allu/actions/action-target-type';
import {ErrorHandlerMock} from '../../../mocks';

const APP_ID = 1;
const COMMENTS_URL = '/api/comments';
const COMMENTS_APP_URL = `/api/applications/${APP_ID}/comments`;


const COMMENT_ONE = new Comment(
  1,
  CommentType.INTERNAL,
  'Test comment one',
  new Date()
);

const COMMENT_TWO = new Comment(
  2,
  CommentType.INVOICING,
  'Test comment two',
  new Date()
);

const COMMENT_NEW = new Comment(
  undefined,
  CommentType.INVOICING,
  'New comment'
);

describe('CommentService', () => {
  let commentService: CommentService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  let errorHandler: ErrorHandlerMock;

  beforeEach(() => {
    const tb = TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: ErrorHandler, useClass: ErrorHandlerMock},
        CommentService
      ]
    });
    commentService = tb.inject(CommentService);
    httpClient = tb.inject(HttpClient);
    httpTestingController = tb.inject(HttpTestingController);
    errorHandler = tb.inject(ErrorHandler) as ErrorHandlerMock;
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('getCommentsFor() should query rest api with matching target type', () => {
    commentService.getCommentsFor(ActionTargetType.Application, APP_ID).subscribe();
    const req = httpTestingController.expectOne(COMMENTS_APP_URL);
    expect(req.request.method).toEqual('GET');
  });

  it('getCommentsFor() should return queried comments', fakeAsync(() => {
    let result: Array<Comment>;
    commentService.getCommentsFor(ActionTargetType.Application, APP_ID).subscribe(r => result = r);
    const req = httpTestingController.expectOne(COMMENTS_APP_URL);

    req.flush([COMMENT_ONE, COMMENT_TWO]);

    tick();
    expect(result[0]).toEqual(COMMENT_ONE, ' COMMENT_ONE should be the first comment');
    expect(result[1]).toEqual(COMMENT_TWO, ' COMMENT_TWO should be the second comment');
  }));

  it('getCommentsFor() should handle errors', fakeAsync(() => {
    let result: Array<Comment>;
    spyOn(errorHandler, 'handle').and.callThrough();
    commentService.getCommentsFor(ActionTargetType.Application, APP_ID).subscribe(r => result = r, error => {});

    const req = httpTestingController.expectOne(COMMENTS_APP_URL);
    req.error(new ErrorEvent('Expected error'));

    tick();
    expect(result).toBeUndefined();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));

  it('saveComment() comment without id should create new', fakeAsync(() => {
    let result: Comment;
    const updatedComment = COMMENT_NEW.copy();
    updatedComment.id = 10;

    commentService.saveComment(ActionTargetType.Application, APP_ID, COMMENT_NEW).subscribe(r => result = r);

    const req = httpTestingController.expectOne(COMMENTS_APP_URL);
    req.flush(updatedComment);

    tick();
    expect(req.request.method).toEqual('POST');
    expect(result).toEqual(updatedComment, 'COMMENT_ONE was not saved');
  }));


  it('saveComment() comment with id should update', fakeAsync(() => {
    let result: Comment;
    commentService.saveComment(ActionTargetType.Application, APP_ID, COMMENT_ONE).subscribe(r => result = r);

    const req = httpTestingController.expectOne(`${COMMENTS_URL}/${COMMENT_ONE.id}`);
    req.flush(COMMENT_ONE);

    tick();
    expect(req.request.method).toEqual('PUT');
    expect(result).toEqual(COMMENT_ONE, 'COMMENT_ONE was not saved');
  }));

  it('saveComment() comment should handle errors', fakeAsync(() => {
    let result: Comment;
    spyOn(errorHandler, 'handle').and.callThrough();
    commentService.saveComment(ActionTargetType.Application, APP_ID, COMMENT_ONE).subscribe(r => result = r, error => {});
    const req = httpTestingController.expectOne(`${COMMENTS_URL}/${COMMENT_ONE.id}`);
    req.error(new ErrorEvent('Expected'));

    tick();
    expect(result).toBeUndefined();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));


  it('remove() should remove comment with matching id', fakeAsync(() => {
    commentService.remove(COMMENT_ONE.id).subscribe();
    const req = httpTestingController.expectOne(`${COMMENTS_URL}/${COMMENT_ONE.id}`);
    tick();
    expect(req.request.method).toEqual('DELETE');
  }));

  it('remove() comment should handle errors', fakeAsync(() => {
    spyOn(errorHandler, 'handle').and.callThrough();
    commentService.remove(COMMENT_ONE.id).subscribe(() => {}, error => {});
    const req = httpTestingController.expectOne(`${COMMENTS_URL}/${COMMENT_ONE.id}`);
    req.error(new ErrorEvent('Expected'));
    tick();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));
});
