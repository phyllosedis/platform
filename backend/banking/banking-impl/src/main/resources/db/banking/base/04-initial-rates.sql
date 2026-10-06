-- Стартовые курсы для локальной отладки (quote за 1 base).
-- Валюты: 0 RUB, 1 USD, 2 EUR, 3 CNY. Обратные пары считаются инверсией 1/rate.
insert into exchange_rate (base_currency, quote_currency, rate)
values
  (1, 0, 90.00000000),   -- USD -> RUB
  (2, 0, 98.00000000),   -- EUR -> RUB
  (3, 0, 12.40000000);   -- CNY -> RUB
