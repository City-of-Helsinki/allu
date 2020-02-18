package fi.hel.allu.supervision.api.domain;

import io.swagger.annotations.ApiModel;

@ApiModel(
    value = "Contact search parameters",
    description =
    "<ul>"
  + "<li><b>Page:</b> Zero-based page index</li>"
  + "<li><b>Page size:</b>Number of items to return</li>"
  + "<li><b>Search parameters:</b> Key-value map containing search parameters (field name as key). Following fields are supported:"
  +   "<ul>"
  +     "<li><b>Key</b>: NAME <b>value:</b> String containing name of the contact</li>"
  +   "</ul>'"
  + "</li>"
  + "<li><b>Sort:</b> List containing sorting parameters (search parameter field key and direction). Following field keys can be used in sort:"
  +   "<ul>"
  +     "<li>NAME</li>"
  +   "</ul>"
  + "</li>"
  + "</ul>"
)
public class ContactSearchParameters extends SearchParameters<ContactSearchParameterField> {
}
