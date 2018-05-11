export class HttpUtil {
  static extractMessage(responseObject: any) {
    if (responseObject.body && responseObject.body !== '') {
      const response = responseObject.json();
      return (response.message) ? response.message : response.status + ' : ' + response.error;
    } else {
      return responseObject.status + ' : ' + responseObject.statusText;
    }
  }
}
