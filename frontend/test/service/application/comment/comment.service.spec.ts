import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import {CommentService} from '../../../../src/app/service/application/comment/comment.service';
import {Comment} from '../../../../src/app/model/application/comment/comment';
import {CommentType} from '../../../../src/app/model/application/comment/comment-type';
import {ErrorHandler} from '../../../../src/app/service/error/error-handler.service';
import {HttpClient} from '@angular/common/http';

const APP_ID = 1;
const COMMENTS_URL = '/api/comments';
const COMMENTS_APP_URL = `/api/comments/applications/${APP_ID}`;


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

const COMMENT_NEW = new Comment(
  undefined,
  CommentType[CommentType.INVOICING],
  'New comment'
);

class ErrorHandlerMock {
  handle(error: any, message?: string) {}
}

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
    commentService = tb.get(CommentService);
    httpClient = tb.get(HttpClient);
    httpTestingController = tb.get(HttpTestingController);
    errorHandler = tb.get(ErrorHandler) as ErrorHandlerMock;
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('getComments() should query rest api with application id', () => {
    commentService.getComments(APP_ID).subscribe();
    const req = httpTestingController.expectOne(COMMENTS_APP_URL);
    expect(req.request.method).toEqual('GET');
  });

  it('getComments() should return queried comments', fakeAsync(() => {
    let result: Array<Comment>;
    commentService.getComments(APP_ID).subscribe(r => result = r);
    const req = httpTestingController.expectOne(COMMENTS_APP_URL);

    req.flush([COMMENT_ONE, COMMENT_TWO]);

    tick();
    expect(result[0]).toEqual(COMMENT_ONE, ' COMMENT_ONE should be the first comment');
    expect(result[1]).toEqual(COMMENT_TWO, ' COMMENT_TWO should be the second comment');
  }));

  it('getComments() should handle errors', fakeAsync(() => {
    let result: Array<Comment>;
    spyOn(errorHandler, 'handle');
    commentService.getComments(APP_ID).subscribe(r => result = r, error => {});

    const req = httpTestingController.expectOne(COMMENTS_APP_URL);
    req.error(new ErrorEvent('Expected error'));

    tick();
    expect(result).toBeUndefined();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));

  it('save() comment without id should create new', fakeAsync(() => {
    let result: Comment;
    const updatedComment = COMMENT_NEW.copy();
    updatedComment.id = 10;

    commentService.save(APP_ID, COMMENT_NEW).subscribe(r => result = r);

    const req = httpTestingController.expectOne(COMMENTS_APP_URL);
    req.flush(updatedComment);

    tick();
    expect(req.request.method).toEqual('POST');
    expect(result).toEqual(updatedComment, 'COMMENT_ONE was not saved');
  }));


  it('save() comment with id should update', fakeAsync(() => {
    let result: Comment;
    commentService.save(APP_ID, COMMENT_ONE).subscribe(r => result = r);

    const req = httpTestingController.expectOne(`${COMMENTS_URL}/${COMMENT_ONE.id}`);
    req.flush(COMMENT_ONE);

    tick();
    expect(req.request.method).toEqual('PUT');
    expect(result).toEqual(COMMENT_ONE, 'COMMENT_ONE was not saved');
  }));

  it('save() comment should handle errors', fakeAsync(() => {
    let result: Comment;
    spyOn(errorHandler, 'handle');
    commentService.save(APP_ID, COMMENT_ONE).subscribe(r => result = r, error => {});
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
    spyOn(errorHandler, 'handle');
    commentService.remove(COMMENT_ONE.id).subscribe(() => {}, error => {});
    const req = httpTestingController.expectOne(`${COMMENTS_URL}/${COMMENT_ONE.id}`);
    req.error(new ErrorEvent('Expected'));
    tick();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));
});
