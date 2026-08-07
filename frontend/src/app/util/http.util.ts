export class HttpUtil {
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  static extractMessage(responseObject: any) {
    if (responseObject.body && responseObject.body !== '') {
      const response = responseObject.json();
      return (response.message) ? response.message : response.status + ' : ' + response.error;
    } else {
      return responseObject.status + ' : ' + responseObject.statusText;
    }
  }
}
