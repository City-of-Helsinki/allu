package fi.hel.allu.security;

public enum Roles {
    /**
     * Allowed to create applications.
     */
    ROLE_CREATE_APPLICATION,
    /**
     * Allowed to process applications.
     */
    ROLE_PROCESS_APPLICATION,
    /**
     * Allowed to view work queues and make and edit work queue filters.
     */
    ROLE_WORK_QUEUE,
    /**
     * Allowed to make decisions.
     */
    ROLE_DECISION,
    /**
     * Allowed to supervise.
     */
    ROLE_SUPERVISE,
    /**
     * Allowed to check invoices.
     */
    ROLE_INVOICING,
    /**
     * Allowed to login and view applications
     */
    ROLE_VIEW,
    /**
     * Allowed to change system configuration.
     */
    ROLE_ADMIN;
}
