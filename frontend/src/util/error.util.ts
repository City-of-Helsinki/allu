export interface HttpError {
  message: string;
  status: number;
};

export class ErrorUtil {
  static extractMessage(errorResponse: any) {
    let error = errorResponse.json();
    return (error.message) ? error.message : error.status + ' : ' + error.error;
  };

  static extractHttpError(errorResponse): HttpError {
    let error = errorResponse.json();
    return {
      message: error.message,
      status: error.status
    };
  }
}

