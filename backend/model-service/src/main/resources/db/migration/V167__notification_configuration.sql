create table allu.notification_configuration (
    id serial primary key,
    notification_type text not null,
    application_states text[] not null,
    specifiers text[] not null
);

-- Status and owner change and events from external system always notified
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('STATUS_CHANGED', array[]::text[], array[]::text[]);
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('COMMENT_ADDED', array[]::text[], '{EXTERNAL_SYSTEM}');
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('EXTERNAL_CUSTOMER_VALIDITY_PERIOD_CHANGED', array[]::text[], array[]::text[]);
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('EXTERNAL_ATTACHMENT', array[]::text[], array[]::text[]);
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('EXTERNAL_OTHER_CHANGE', array[]::text[], array[]::text[]);

-- Changes notified in states before decision
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('INVOICE_RECIPIENT_CHANGED', '{PENDING_CLIENT, PRE_RESERVED, PENDING, WAITING_INFORMATION, INFORMATION_RECEIVED, HANDLING, NOTE, RETURNED_TO_PREPARATION, WAITING_CONTRACT_APPROVAL, DECISIONMAKING}', array[]::text[]);
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('CONTENT_CHANGED', '{PENDING_CLIENT, PRE_RESERVED, PENDING, WAITING_INFORMATION, INFORMATION_RECEIVED, HANDLING, NOTE, RETURNED_TO_PREPARATION, WAITING_CONTRACT_APPROVAL, DECISIONMAKING}', array[]::text[]);
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('COMMENT_ADDED', '{PENDING_CLIENT, PRE_RESERVED, PENDING, WAITING_INFORMATION, INFORMATION_RECEIVED, HANDLING, NOTE, RETURNED_TO_PREPARATION, WAITING_CONTRACT_APPROVAL, DECISIONMAKING}', array[]::text[]);
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('ATTACHMENT_ADDED', '{PENDING_CLIENT, PRE_RESERVED, PENDING, WAITING_INFORMATION, INFORMATION_RECEIVED, HANDLING, NOTE, RETURNED_TO_PREPARATION, WAITING_CONTRACT_APPROVAL, DECISIONMAKING}', '{ADDED_BY_CUSTOMER, ADDED_BY_HANDLER, DEFAULT, DEFAULT_IMAGE, DEFAULT_TERMS, STATEMENT, OTHER}');
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('SUPERVISION_ADDED', '{PENDING_CLIENT, PRE_RESERVED, PENDING, WAITING_INFORMATION, INFORMATION_RECEIVED, HANDLING, NOTE, RETURNED_TO_PREPARATION, WAITING_CONTRACT_APPROVAL, DECISIONMAKING}', array[]::text[]);
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('SUPERVISION_APPROVED', '{PENDING_CLIENT, PRE_RESERVED, PENDING, WAITING_INFORMATION, INFORMATION_RECEIVED, HANDLING, NOTE, RETURNED_TO_PREPARATION, WAITING_CONTRACT_APPROVAL, DECISIONMAKING}', array[]::text[]);
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('SUPERVISION_REMOVED', '{PENDING_CLIENT, PRE_RESERVED, PENDING, WAITING_INFORMATION, INFORMATION_RECEIVED, HANDLING, NOTE, RETURNED_TO_PREPARATION, WAITING_CONTRACT_APPROVAL, DECISIONMAKING}', array[]::text[]);
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('SUPERVISION_UPDATED', '{PENDING_CLIENT, PRE_RESERVED, PENDING, WAITING_INFORMATION, INFORMATION_RECEIVED, HANDLING, NOTE, RETURNED_TO_PREPARATION, WAITING_CONTRACT_APPROVAL, DECISIONMAKING}', array[]::text[]);
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('CUSTOMER_VALIDITY_PERIOD_CHANGED','{PENDING_CLIENT, PRE_RESERVED, PENDING, WAITING_INFORMATION, INFORMATION_RECEIVED, HANDLING, NOTE, RETURNED_TO_PREPARATION, WAITING_CONTRACT_APPROVAL, DECISIONMAKING}', array[]::text[]);

-- Changes notified only in decision making state
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('ATTACHMENT_ADDED', '{DECISIONMAKING}', '{SUPERVISION}');
insert into allu.notification_configuration (notification_type, application_states, specifiers)
values ('SUPERVISION_REJECTED', '{DECISIONMAKING}', array[]::text[]);
