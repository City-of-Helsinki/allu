import {ErrorType} from './error-type';
/**
 * Class to wrap error identifier
 * and message
 */
export class ErrorInfo {
  constructor(type: ErrorType)
  constructor(type: ErrorType, message: string)
  constructor(public type: ErrorType, public message?: string) {};

  public equals(other: ErrorInfo): boolean {
    if (other !== undefined) {
      return this.type === other.type && this.message === other.message;
    }
    return false;
  }
}
