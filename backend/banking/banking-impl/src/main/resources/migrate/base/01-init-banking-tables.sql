
create table users (
    id uuid not NULL,
    name varchar(30) not null
);

create table account (
    id uuid default gen_random_uuid() primary key,
    user_id uuid not null,
    account_number varchar(20) unique not null,
    currency int not null,
    status int not null,
    "type" int not null,
    amount numeric(20,2) not null default 0.0,

    constraint fk_user foreign key (user_id) references users(id)
);

create table transactions (
    id uuid default gen_random_uuid() primary key,
    from_account_id uuid not null,
    to_account_id uuid not null,
    type int not null,
    status int not null,
    created_at timestamp with time zone default now(),
    amount numeric(20, 2) not null,

    constraint fk_from_account_id foreign key (from_account_id) references account(id),
    constraint fk_to_account_id foreign key (to_account_id) references account(id),
    constraint chk_transactions_participants check (from_account_id is not null or to_account_id is not null)
    -- центральный банк производит эмиссию денег, поэтому получатель и отправитель никогда не должны быть null
);

create index idx_account_user_id on account (user_id);
create index idx_account_number on account (account_number);

create index idx_transactions_from_account on transactions (from_account_id, created_at desc);
create index idx_transactions_to_account on transactions (to_account_id, created_at desc);
