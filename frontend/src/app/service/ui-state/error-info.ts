import {ErrorType} from './error-type';
import {HttpResponse} from '../../util/http-response';

/**
 * Class to wrap http errors with messageToReadable
 */
export class ErrorInfo {
  // type is still kept since old implementations use it.
  // It will be removed when all http-error handling is updated
  constructor(public type: ErrorType, public message?: string, public response?: HttpResponse) {}

  static of(response: HttpResponse, message: string): ErrorInfo {
    return new ErrorInfo(undefined, message, response);
  }
}
