package fi.hel.allu.supervision.api.domain;

import io.swagger.annotations.ApiModel;

@ApiModel(
     value = "Supervision task search parameters",
     description =
        "<ul>"
      + "<li><b>Intersecting geometry:</b> Geometry intersecting with application geometry</li>"
      + "<li><b>Page:</b> Zero-based page index</li>"
      + "<li><b>Page size:</b>Number of items to return</li>"
      + "<li><b>Search parameters:</b> Key-value map containing search parameters (field name as key). Following fields are supported:"
      +   "<ul>"
      +     "<li><b>Key</b>: APPLICATION_IDENTIFIER, <b>value:</b> String containing application identifier (hakemustunniste)</li>"
      +     "<li><b>Key</b>: OWNER_USERNAME, <b>value:</b> Comma-separated string containing user names of supervision task owners</li>"
      +     "<li><b>Key</b>: OWNER_ID, <b>value:</b> Comma-separated string containing IDs of supervision task owners</li>"
      +     "<li><b>Key</b>: STATUS, <b>value:</b> Comma-separated string containing statuses of supervision tasks</li>"
      +     "<li><b>Key</b>: TYPE, <b>value:</b> Comma-separated string containing types of supervision tasks</li>"
      +     "<li><b>Key</b>: VALID_AFTER, <b>value:</b> Date-time with an offset, eg '2011-12-03T10:15:30+01:00'</li>"
      +     "<li><b>Key</b>: VALID_BEFORE, <b>value:</b> Date-time with an offset, eg '2011-12-03T10:15:30+01:00'</li>"
      +   "</ul>"
      + "</li>"
      + "<li><b>Sort:</b> List containing sorting parameters (search parameter field key and direction). All search parameter field keys can be used in sort.</li>"
      + "</ul>"
)
public class SupervisionTaskSearchParameters extends SearchParameters<SupervisionTaskSearchParameterField> {
}
