"use strict";
var time_util_1 = require("../../util/time.util");
var sort_1 = require("../../model/common/sort");
var option_1 = require("../../util/option");
var enumFields = [
    'status',
    'type'
];
var START_TIME_FIELD = 'startTime';
var END_TIME_FIELD = 'endTime';
var QueryParametersMapper = (function () {
    function QueryParametersMapper() {
    }
    QueryParametersMapper.mapApplicationQueryFrontend = function (query) {
        return (query) ?
            {
                queryParameters: QueryParametersMapper.mapApplicationParameters(query),
                sort: QueryParametersMapper.mapSort(query)
            } : undefined;
    };
    QueryParametersMapper.mapProjectQueryFrontend = function (query) {
        return option_1.Some(query).map(function (q) {
            return {
                queryParameters: QueryParametersMapper.mapProjectParameters(query),
                sort: QueryParametersMapper.mapSort(query)
            };
        }).orElse(undefined);
    };
    QueryParametersMapper.mapSort = function (query) {
        return (query.sort && query.sort.field && query.sort.direction !== undefined) ?
            {
                field: QueryParametersMapper.getBackendSortField(query.sort.field),
                direction: sort_1.Direction[query.sort.direction]
            } : undefined;
    };
    QueryParametersMapper.mapApplicationParameters = function (query) {
        var queryParameters = [];
        QueryParametersMapper.mapParameter(queryParameters, 'locations.streetAddress', QueryParametersMapper.removeExtraWhitespace(query.address));
        QueryParametersMapper.mapParameter(queryParameters, 'applicant.name', QueryParametersMapper.removeExtraWhitespace(query.applicant));
        QueryParametersMapper.mapParameter(queryParameters, 'contacts.name', QueryParametersMapper.removeExtraWhitespace(query.contact));
        QueryParametersMapper.mapArrayParameter(queryParameters, 'handler.userName', query.handler);
        QueryParametersMapper.mapParameter(queryParameters, 'applicationId', QueryParametersMapper.removeExtraWhitespace(query.applicationId));
        QueryParametersMapper.mapArrayParameter(queryParameters, 'status', query.status);
        QueryParametersMapper.mapArrayParameter(queryParameters, 'locations.cityDistrictId', query.districts);
        QueryParametersMapper.mapArrayParameter(queryParameters, 'applicationTags', query.tags);
        QueryParametersMapper.mapArrayParameter(queryParameters, 'type', query.type);
        QueryParametersMapper.mapParameter(queryParameters, '_all', query.freeText);
        QueryParametersMapper.mapDateParameter(queryParameters, START_TIME_FIELD, time_util_1.MIN_DATE, query.endTime);
        QueryParametersMapper.mapDateParameter(queryParameters, END_TIME_FIELD, query.startTime, time_util_1.MAX_DATE);
        option_1.Some(query.projectId).do(function (projectId) { return QueryParametersMapper.mapParameter(queryParameters, 'projectId', projectId.toString()); });
        return queryParameters;
    };
    QueryParametersMapper.mapProjectParameters = function (query) {
        var queryParameters = [];
        option_1.Some(query.id).do(function (id) { return QueryParametersMapper.mapParameter(queryParameters, 'id', id.toString()); });
        QueryParametersMapper.mapDateParameter(queryParameters, START_TIME_FIELD, time_util_1.MIN_DATE, query.endTime);
        QueryParametersMapper.mapDateParameter(queryParameters, END_TIME_FIELD, query.startTime, time_util_1.MAX_DATE);
        QueryParametersMapper.mapArrayParameter(queryParameters, 'cityDistricts', query.districts);
        QueryParametersMapper.mapParameter(queryParameters, 'ownerName', QueryParametersMapper.removeExtraWhitespace(query.ownerName));
        option_1.Some(query.onlyActive).do(function (onlyActive) { return QueryParametersMapper.mapProjectActivityParameter(queryParameters, onlyActive); });
        option_1.Some(query.creator).do(function (creator) { return QueryParametersMapper.mapParameter(queryParameters, 'creator', creator.toString()); });
        return queryParameters;
    };
    QueryParametersMapper.mapParameter = function (queryParameters, parameterName, parameterValue) {
        if (parameterValue) {
            queryParameters.push(QueryParametersMapper.createParameter(parameterName, parameterValue));
        }
    };
    QueryParametersMapper.mapArrayParameter = function (queryParameters, parameterName, parameterValue) {
        if (parameterValue) {
            var filteredParameterValue = parameterValue.filter(function (value) { return !!value; });
            if (filteredParameterValue.length !== 0) {
                queryParameters.push(QueryParametersMapper.createArrayParameter(parameterName, filteredParameterValue));
            }
        }
    };
    QueryParametersMapper.mapDateParameter = function (queryParameters, parameterName, startDate, endDate) {
        if (startDate && endDate) {
            queryParameters.push(QueryParametersMapper.createDateParameter(parameterName, startDate, endDate));
        }
    };
    QueryParametersMapper.mapProjectActivityParameter = function (queryParameters, onlyActive) {
        if (onlyActive) {
            var startTimeParameter = QueryParametersMapper.createDateParameter(START_TIME_FIELD, time_util_1.MIN_DATE, new Date());
            var endTimeParameter = QueryParametersMapper.createDateParameter(END_TIME_FIELD, new Date(), time_util_1.MAX_DATE);
            queryParameters.push(startTimeParameter);
            queryParameters.push(endTimeParameter);
        }
    };
    QueryParametersMapper.createArrayParameter = function (parameterName, parameterValue) {
        return {
            fieldName: QueryParametersMapper.getBackendValueField(parameterName),
            fieldValue: undefined,
            fieldMultiValue: parameterValue,
            startDateValue: undefined,
            endDateValue: undefined
        };
    };
    QueryParametersMapper.createParameter = function (parameterName, parameterValue) {
        return {
            fieldName: QueryParametersMapper.getBackendValueField(parameterName),
            fieldValue: parameterValue,
            fieldMultiValue: undefined,
            startDateValue: undefined,
            endDateValue: undefined
        };
    };
    QueryParametersMapper.createDateParameter = function (parameterName, startDate, endDate) {
        return {
            fieldName: QueryParametersMapper.getBackendValueField(parameterName),
            fieldValue: undefined,
            fieldMultiValue: undefined,
            startDateValue: startDate.toISOString(),
            endDateValue: endDate.toISOString()
        };
    };
    QueryParametersMapper.getBackendSortField = function (field) {
        if (enumFields.indexOf(field) > -1) {
            return field + '.ordinal';
        }
        else {
            return field;
        }
    };
    QueryParametersMapper.getBackendValueField = function (field) {
        if (enumFields.indexOf(field) > -1) {
            return field + '.value';
        }
        else {
            return field;
        }
    };
    QueryParametersMapper.removeExtraWhitespace = function (str) {
        var retVal = undefined;
        if (str) {
            retVal = str.trim();
            retVal = retVal.replace('\s+', ' ');
        }
        return retVal;
    };
    return QueryParametersMapper;
}());
exports.QueryParametersMapper = QueryParametersMapper;
