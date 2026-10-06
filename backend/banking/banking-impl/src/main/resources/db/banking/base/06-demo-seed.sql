-- Демо-наполнение: те же userId, что и в auth (источник правды).
insert into users (id, name)
values
  ('11111111-1111-1111-1111-111111111111'::uuid, 'neo'),
  ('22222222-2222-2222-2222-222222222222'::uuid, 'trinity')
on conflict (id) do nothing;

insert into account (id, user_id, account_number, currency, status, "type", amount)
values
  (gen_random_uuid(), '11111111-1111-1111-1111-111111111111'::uuid, '408810100001', 0, 0, 4, 10000.00),
  (gen_random_uuid(), '11111111-1111-1111-1111-111111111111'::uuid, '408840100001', 1, 0, 4, 500.00),
  (gen_random_uuid(), '22222222-2222-2222-2222-222222222222'::uuid, '408810100002', 0, 0, 4, 7500.00);
