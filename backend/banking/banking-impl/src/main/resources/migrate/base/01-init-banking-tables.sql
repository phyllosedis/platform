
create table "user"(
    id uuid default gen_random_uuid() primary key,
    name varchar(30) not null
);

create table "account" (
    id uuid default gen_random_uuid() primary key,
    user_id uuid not null,
    account_number varchar(20) unique not null,
    currency int not null,
    status int not null,

    constraint fk_user foreign key (user_id) references "user"(id) on delete cascade
);

create table "transaction"(
    id uuid default gen_random_uuid() primary key,
    from_account_id uuid not null,
    to_account_id uuid not null,
    type int not null,
    status int not null,
    created_at timestamp default now()::timestamp,

    constraint fk_from_account_id foreign key (from_account_id) references "account"(id),
    constraint fk_to_account_id foreign key (to_account_id) references "account"(id)
);