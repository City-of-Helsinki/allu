update allu.user set user_name = lower(user_name);
alter table allu.user add constraint allu_user_user_name_lowercase_ck check (user_name = lower(user_name));