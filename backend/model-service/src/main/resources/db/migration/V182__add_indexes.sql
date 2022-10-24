CREATE INDEX supervision_task_owner_index ON supervision_task (owner_id);

CREATE INDEX supervision_task_type_index ON supervision_task (type);

CREATE INDEX change_history_application_id_index ON change_history (application_id);