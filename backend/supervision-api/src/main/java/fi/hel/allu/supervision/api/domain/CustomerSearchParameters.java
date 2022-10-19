package fi.hel.allu.supervision.api.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Customer search parameters",
    description =
    "<ul>"
  + "<li><b>Page:</b> Zero-based page index</li>"
  + "<li><b>Page size:</b>Number of items to return</li>"
  + "<li><b>Search parameters:</b> Key-value map containing search parameters (field name as key). Following fields are supported:"
  +   "<ul>"
  +     "<li><b>Key</b>: NAME, <b>value:</b> String containing name of the customer</li>"
  +     "<li><b>Key</b>: REGISTRY_KEY, <b>value:</b> String containing registry key of the customer</li>"
  +     "<li><b>Key</b>: IS_ACTIVE, <b>value:</b> Boolean indicating the activity status of the customer</li>"
  +   "</ul>'"
  + "</li>"
  + "<li><b>Sort:</b> List containing sorting parameters (search parameter field key and direction). Following field keys can be used in sort:"
  +   "<ul>"
  +     "<li>NAME</li>"
  +     "<li>REGISTRY_KEY</li>"
  +     "<li>IS_ACTIVE</li>"
  +   "</ul>"
  + "</li>"
  + "</ul>"
)
public class CustomerSearchParameters extends SearchParameters<CustomerSearchParameterField> {
}
