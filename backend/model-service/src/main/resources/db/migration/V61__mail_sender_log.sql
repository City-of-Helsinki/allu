create table allu.mail_sender_log (
  id serial primary key,
  subject text,
  receivers text[],
  sent_time timestamp with time zone not null,
  sent_failed boolean not null,
  error_message text
);
