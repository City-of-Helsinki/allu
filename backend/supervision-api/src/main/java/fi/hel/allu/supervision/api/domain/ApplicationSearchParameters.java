package fi.hel.allu.supervision.api.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Application search parameters",
    description =
    "<ul>"
  + "<li><b>Intersecting geometry:</b> Geometry intersecting with application geometry</li>"
  + "<li><b>Page:</b> Zero-based page index</li>"
  + "<li><b>Page size:</b>Number of items to return</li>"
  + "<li><b>Search parameters:</b> Key-value map containing search parameters (field name as key). Following fields are supported:"
  +   "<ul>"
  +     "<li><b>Key</b>: APPLICATION_IDENTIFIER, <b>value:</b> String containing (part of) application identifier (hakemustunniste)</li>"
  +     "<li><b>Key</b>: NAME, <b>value:</b> String containing name of the application</li>"
  +     "<li><b>Key</b>: TYPE, <b>value:</b> Comma-separated string containing types of applications</li>"
  +     "<li><b>Key</b>: STATUS, <b>value:</b> Comma-separated string containing statuses applications</li>"
  +     "<li><b>Key</b>: DISTRICTS, <b>value:</b> Comma-separated string containing IDs of city districts</li>"
  +     "<li><b>Key</b>: OWNER, <b>value:</b> Comma-separated string containing user names of application owners</li>"
  +     "<li><b>Key</b>: ADDRESS, <b>value:</b> String containing street address of the application</li>"
  +     "<li><b>Key</b>: APPLICANT_NAME, <b>value:</b> String containing (part of) name of the applicant</li>"
  +     "<li><b>Key</b>: CONTACT_NAME, <b>value:</b> String containing (part of) name of the applicant's contact person</li>"
  +     "<li><b>Key</b>: VALID_AFTER, <b>value:</b> Date-time with an offset, eg '2011-12-03T10:15:30+01:00'</li>"
  +     "<li><b>Key</b>: VALID_BEFORE, <b>value:</b> Date-time with an offset, eg '2011-12-03T10:15:30+01:00'</li>"
  +     "<li><b>Key</b>: TAGS, <b>value:</b> Comma-separated string containing application tag names</li>"
  +     "<li><b>Key</b>: PROJECT_ID, <b>value:</b> Project ID (number)</li>"
  +     "<li><b>Key</b>: PROJECT_IDENTIFIER, <b>value:</b> Project identifier (hanketunniste, string)</li>"
  +   "</ul>'"
  + "</li>"
  + "<li><b>Sort:</b> List containing sorting parameters (search parameter field key and direction). Following field keys can be used in sort:"
  +   "<ul>"
  +     "<li>APPLICATION_IDENTIFIER</li>"
  +     "<li>NAME</li>"
  +     "<li>TYPE</li>"
  +     "<li>STATUS</li>"
  +     "<li>OWNER</li>"
  +     "<li>APPLICANT_NAME</li>"
  +     "<li>VALID_AFTER</li>"
  +     "<li>VALID_BEFORE</li>"
  +   "</ul>"
  + "</li>"
  + "</ul>"
)
public class ApplicationSearchParameters extends SearchParametersWithGeometry<ApplicationSearchParameterField> {
}
