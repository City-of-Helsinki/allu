package fi.hel.allu.supervision.api.domain;

import io.swagger.annotations.ApiModel;

@ApiModel(
    value = "Project search parameters",
    description =
       "<ul>"
     + "<li><b>Intersecting geometry:</b> Geometry intersecting with one of the project's application geometry</li>"
     + "<li><b>Page:</b> Zero-based page index</li>"
     + "<li><b>Page size:</b>Number of items to return</li>"
     + "<li><b>Search parameters:</b> Key-value map containing search parameters (field name as key). Following fields are supported:"
     +   "<ul>"
     +     "<li><b>Key</b>: IDENTIFIER, <b>value:</b> Project identfier (hanketunniste)</li>"
     +     "<li><b>Key</b>: NAME, <b>value:</b> Name of the project</li>"
     +     "<li><b>Key</b>: CUSTOMER_REFERENCE, <b>value:</b> Customer reference</li>"
     +     "<li><b>Key</b>: OWNER_NAME, <b>value:</b> Name of the owner (customer)</li>"
     +     "<li><b>Key</b>: VALID_AFTER, <b>value:</b> Date-time with an offset, eg '2011-12-03T10:15:30+01:00'</li>"
     +     "<li><b>Key</b>: VALID_BEFORE, <b>value:</b> Date-time with an offset, eg '2011-12-03T10:15:30+01:00'</li>"
     +     "<li><b>Key</b>: APPLICATION_IDENTIFIER, <b>value:</b> Identifier of the application belonging to project</li>"
     +   "</ul>"
     + "</li>"
     + "<li><b>Sort:</b> List containing sorting parameters (search parameter field key and direction). Following field keys can be used in sort:"
     +   "<ul>"
     +     "<li>IDENTIFIER</li>"
     +     "<li>OWNER_NAME</li>"
     +     "<li>VALID_AFTER</li>"
     +     "<li>VALID_BEFORE</li>"
     +   "</ul>"
     + "</li>"
     + "</ul>"
)
public class ProjectSearchParameters extends SearchParameters<ProjectSearchParameterField> {

}
