import {HttpResponse} from './http-response';
export class HttpUtil {
  static extractMessage(responseObject: any) {
    if (responseObject.body && responseObject.body !== '') {
      let response = responseObject.json();
      return (response.message) ? response.message : response.status + ' : ' + response.error;
    } else {
      return responseObject.status + ' : ' + responseObject.statusText;
    }
  };

  static extractHttpResponse(responseObject: any): HttpResponse {
    let response = undefined;
    if (responseObject.body && responseObject.body !== '') {
      response = responseObject.json();
    } else {
      response = responseObject;
    }
    return new HttpResponse(response.status, response.message);
  }
}
