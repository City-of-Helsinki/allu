import {ErrorType} from './error-type';
import {HttpResponse} from '../../util/http-response';

/**
 * Class to wrap http errors with message
 */
export class ErrorInfo {
  // type is still kept since old implementations use it.
  // It will be removed when all http-error handling is updated
  constructor(type?: ErrorType)
  constructor(type?: ErrorType, message?: string)
  constructor(type?: ErrorType, message?: string, response?: HttpResponse)
  constructor(public type: ErrorType, public message?: string, public response?: HttpResponse) {};

  static of(response: HttpResponse, message: string): ErrorInfo {
    return new ErrorInfo(undefined, message, response);
  }
}
