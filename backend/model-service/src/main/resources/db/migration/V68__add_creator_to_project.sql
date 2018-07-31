alter table allu.project add column creator_id integer references allu.user(id);

do
$$
declare
  p record;
  u integer;
begin
  for p in
  select * from allu.project where creator_id is null
  loop
    select user_id into u from allu.change_history where project_id = p.id and change_type = 'CREATED';
    update allu.project set creator_id = u where id = p.id;
  end loop;
end;
$$
LANGUAGE plpgsql;

alter table allu.project alter column creator_id set not null;
