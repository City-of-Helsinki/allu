package fi.hel.allu.search.util;

/**
 * Constants that are shared between controller classes.
 */
public class Constants {

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 100;
    public static final String APPLICATION_INDEX_ALIAS = "applications";
    public static final String PROJECT_INDEX_ALIAS = "projects";
    public static final String CUSTOMER_INDEX_ALIAS = "customers";
    public static final String CONTACT_INDEX_ALIAS = "contacts";
    public static final String PROPERTIES_INDEX_ALIAS = "properties";
    public static final String PROJECT_TYPE_NAME = "project";
    public static final String CUSTOMER_TYPE_NAME = "customer";
    public static final String SUPERVISION_TASK_INDEX = "supervision_tasks";

    private Constants() {
        throw new IllegalStateException("Utility class");
    }
}