import {TestBed, tick, fakeAsync} from '@angular/core/testing';
import {AuthHttp} from 'angular2-jwt';
import {
  BaseRequestOptions,
  ConnectionBackend,
  Http,
  RequestOptions,
  HttpModule,
  Response,
  ResponseOptions,
  ResponseType,
  RequestMethod
} from '@angular/http';
import {MockBackend} from '@angular/http/testing';
import {CommentService} from '../../../../src/service/application/comment/comment.service';
import {Comment} from '../../../../src/model/application/comment/comment';
import {CommentType} from '../../../../src/model/application/comment/comment-type';
import {HttpStatus, HttpResponse} from '../../../../src/util/http-response';
import {ErrorHandler} from '../../../../src/service/error/error-handler.service';
import createSpy = jasmine.createSpy;

const COMMENTS_URL = '/api/comments';
const COMMENTS_APP_URL = COMMENTS_URL + '/applications/:appId';

const APP_ID = 1;
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

const ERROR_RESPONSE = new Response(new ResponseOptions({
  type: ResponseType.Error,
  status: 404
}));

class ErrorHandlerMock {
  handle(error: any, message?: string) {}
};

describe('CommentService', () => {
  let commentService: CommentService;
  let backend: MockBackend;
  let errorHandler: ErrorHandlerMock;
  let lastConnection: any;

  beforeEach(() => {
    let tb = TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        MockBackend,
        BaseRequestOptions,
        { provide: ConnectionBackend, useClass: MockBackend },
        { provide: RequestOptions, useClass: BaseRequestOptions },
        Http,
        { provide: AuthHttp, useExisting: Http, deps: [Http] },
        { provide: ErrorHandler, useClass: ErrorHandlerMock},
        CommentService
      ]
    });
    commentService = tb.get(CommentService);
    backend = tb.get(ConnectionBackend) as MockBackend;
    backend.connections.subscribe((connection: any) => lastConnection = connection);
    errorHandler = tb.get(ErrorHandler) as ErrorHandlerMock;
  });

  it('getComments() should query rest api with application id', () => {
    commentService.getComments(APP_ID);
    expect(lastConnection).toBeDefined('No service');
    expect(lastConnection.request.url).toMatch(COMMENTS_APP_URL.replace(':appId', String(APP_ID)));
  });

  it('getComments() should return queried comments', fakeAsync(() => {
    let result: Array<Comment>;
    commentService.getComments(APP_ID).subscribe(r => result = r);
    lastConnection.mockRespond(new Response(new ResponseOptions({
      body: JSON.stringify([COMMENT_ONE, COMMENT_TWO])
    })));
    tick();
    expect(result[0]).toEqual(COMMENT_ONE, ' COMMENT_ONE should be the first comment');
    expect(result[1]).toEqual(COMMENT_TWO, ' COMMENT_TWO should be the second comment');
  }));

  it('getComments() should handle errors', fakeAsync(() => {
    let result: Array<Comment>;
    spyOn(errorHandler, 'handle');
    commentService.getComments(APP_ID).subscribe(r => result = r, error => {});
    lastConnection.mockError(ERROR_RESPONSE);
    tick();
    expect(result).toBeUndefined();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));

  it('save() comment without id should create new', fakeAsync(() => {
    let result: Comment;
    let updatedComment = COMMENT_NEW.copy();
    updatedComment.id = 10;

    commentService.save(APP_ID, COMMENT_NEW).subscribe(r => result = r);
    lastConnection.mockRespond(new Response(new ResponseOptions({
      body: JSON.stringify(updatedComment)
    })));
    tick();
    expect(lastConnection.request.method).toEqual(RequestMethod.Post);
    expect(result).toEqual(updatedComment, 'COMMENT_ONE was not saved');
  }));

  it('save() comment with id should update', fakeAsync(() => {
    let result: Comment;
    commentService.save(APP_ID, COMMENT_ONE).subscribe(r => result = r);
    lastConnection.mockRespond(new Response(new ResponseOptions({
      body: JSON.stringify(COMMENT_ONE)
    })));
    tick();
    expect(lastConnection.request.method).toEqual(RequestMethod.Put);
    expect(result).toEqual(COMMENT_ONE, 'COMMENT_ONE was not saved');
  }));

  it('save() comment should handle errors', fakeAsync(() => {
    let result: Comment;
    spyOn(errorHandler, 'handle');
    commentService.save(APP_ID, COMMENT_ONE).subscribe(r => result = r, error => {});
    lastConnection.mockError(ERROR_RESPONSE);
    tick();
    expect(result).toBeUndefined();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));

  it('remove() should remove comment with matching id', fakeAsync(() => {
    let result: HttpResponse;
    commentService.remove(COMMENT_ONE.id).subscribe(r => result = r);
    lastConnection.mockRespond(new Response(new ResponseOptions({
      status: 200
    })));
    tick();
    expect(lastConnection.request.method).toEqual(RequestMethod.Delete);
    expect(result.status).toEqual(HttpStatus.OK);
  }));

  it('remove() comment should handle errors', fakeAsync(() => {
    let result: HttpResponse;
    spyOn(errorHandler, 'handle');
    commentService.remove(COMMENT_ONE.id).subscribe(r => result = r, error => {});
    lastConnection.mockError(ERROR_RESPONSE);
    tick();
    expect(result).toBeUndefined();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));
});
