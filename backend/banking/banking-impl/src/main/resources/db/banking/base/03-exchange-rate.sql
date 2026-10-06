
create table exchange_rate (
    id uuid default gen_random_uuid() primary key,
    base_currency int not null,
    quote_currency int not null,
    rate numeric(20, 8) not null,
    valid_from timestamp with time zone default now() not null,

    constraint uq_exchange_rate_pair unique (base_currency, quote_currency),
    constraint chk_exchange_rate_positive check (rate > 0)
);

create index idx_exchange_rate_pair on exchange_rate (base_currency, quote_currency);
