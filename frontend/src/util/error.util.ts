export class ErrorUtil {
  static extractMessage(errorResponse: any) {
    let error = errorResponse.json();
    return (error.message) ? error.message : error.status + ' : ' + error.error;
  };
}

