alter table allu.configuration add column key text;
update allu.configuration set key=type;
update allu.configuration set type='EMAIL' where key='CUSTOMER_NOTIFICATION_RECEIVER_EMAIL';
update allu.configuration set type='EMAIL' where key='INVOICE_NOTIFICATION_RECEIVER_EMAIL';
alter table allu.configuration alter column key set not null;
