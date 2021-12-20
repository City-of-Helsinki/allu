DO $$
BEGIN
        IF 0 != (SELECT 1 FROM allu.user WHERE user_name = 'alluvalv') then
            INSERT INTO allu.user_role values (DEFAULT , 7, 'ROLE_CREATE_APPLICATION');
end if;
END $$;
