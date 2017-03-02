"use strict";
var http_response_1 = require("./http-response");
var HttpUtil = (function () {
    function HttpUtil() {
    }
    HttpUtil.extractMessage = function (responseObject) {
        if (responseObject.body && responseObject.body !== '') {
            var response = responseObject.json();
            return (response.message) ? response.message : response.status + ' : ' + response.error;
        }
        else {
            return responseObject.status + ' : ' + responseObject.statusText;
        }
    };
    ;
    HttpUtil.extractHttpResponse = function (responseObject) {
        var response = undefined;
        if (responseObject.body && responseObject.body !== '') {
            response = responseObject.json();
        }
        else {
            response = responseObject;
        }
        return new http_response_1.HttpResponse(response.status, response.message);
    };
    return HttpUtil;
}());
exports.HttpUtil = HttpUtil;
