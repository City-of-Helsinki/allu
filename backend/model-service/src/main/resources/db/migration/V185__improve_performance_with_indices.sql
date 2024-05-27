CREATE INDEX field_change_change_history_id_index ON field_change (change_history_id);

CREATE INDEX change_history_customer_id_index ON change_history (customer_id);

CREATE INDEX change_history_project_id_index ON change_history (project_id);

CREATE INDEX application_attachment_application_id_index ON application_attachment (application_id);

CREATE INDEX application_attachment_attachment_id_index ON application_attachment (attachment_id);

CREATE INDEX distribution_entry_application_id_index ON distribution_entry (application_id);

CREATE INDEX application_customer_contact_application_customer_id_index ON application_customer_contact (application_customer_id);

CREATE INDEX application_customer_customer_id_index ON application_customer (customer_id);

CREATE INDEX application_customer_application_id_index ON application_customer (application_id);
