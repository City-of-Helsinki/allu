ALTER TABLE allu.external_user rename token to password;
ALTER TABLE allu.external_user ALTER COLUMN password DROP NOT NULL;
ALTER TABLE allu.external_user ALTER COLUMN email_address DROP NOT NULL;