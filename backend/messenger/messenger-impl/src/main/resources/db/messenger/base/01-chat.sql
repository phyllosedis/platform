
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

create table chat_message (
    id uuid default gen_random_uuid() primary key,
    chat_id uuid not null,
    author_id uuid not null,
    content varchar(2000) not null,
    created_at timestamp with time zone default now() not null
);

create index idx_chat_message_chat on chat_message (chat_id, created_at);

create table member_profile (
    user_id uuid primary key,
    nickname varchar(30) not null
);
