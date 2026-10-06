-- Демо-наполнение: профили тех же userId, что и в auth, + переписка в демо-чате.
insert into member_profile (user_id, nickname)
values
  ('11111111-1111-1111-1111-111111111111'::uuid, 'neo'),
  ('22222222-2222-2222-2222-222222222222'::uuid, 'trinity')
on conflict (user_id) do nothing;

insert into chat_message (chat_id, author_id, content)
values
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'::uuid, '11111111-1111-1111-1111-111111111111'::uuid, 'Привет! Это первое сообщение демо-чата.'),
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'::uuid, '22222222-2222-2222-2222-222222222222'::uuid, 'Привет! Мессенджер работает.'),
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'::uuid, '11111111-1111-1111-1111-111111111111'::uuid, 'Проверь перевод: скинь мне 10 USD с конвертацией.');
