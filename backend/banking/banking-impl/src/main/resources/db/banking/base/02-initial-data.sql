insert into users (id, name)
values
('00000000-0000-0000-0000-000000000000'::uuid, 'Центральный банк');

INSERT INTO account (id, user_id, account_number, currency, status, "type", amount)
VALUES
  (gen_random_uuid(), '00000000-0000-0000-0000-000000000000'::uuid, '999810000000', 0, 0, 0, 0.00), -- RUB (810)
  (gen_random_uuid(), '00000000-0000-0000-0000-000000000000'::uuid, '999840000000', 1, 0, 0, 0.00), -- USD (840)
  (gen_random_uuid(), '00000000-0000-0000-0000-000000000000'::uuid, '999978000000', 2, 0, 0, 0.00), -- EUR (978)
  (gen_random_uuid(), '00000000-0000-0000-0000-000000000000'::uuid, '999156000000', 3, 0, 0, 0.00); -- CNY (156)