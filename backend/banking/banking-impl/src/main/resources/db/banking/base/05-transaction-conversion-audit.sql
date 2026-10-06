
-- Аудит конвертации: сколько списано (amount) и сколько зачислено (converted_amount) по какому курсу (rate_used).
-- Для однокалютных переводов converted_amount = amount, rate_used = 1.
alter table transactions
    add column converted_amount numeric(20, 2),
    add column rate_used numeric(20, 8);
