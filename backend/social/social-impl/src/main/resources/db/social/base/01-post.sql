
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

create table post (
    id uuid default gen_random_uuid() primary key,
    author_id uuid not null,
    content varchar(2000) not null,
    created_at timestamp with time zone default now() not null
);

create index idx_post_author on post (author_id, created_at desc);

create table author_profile (
    user_id uuid primary key,
    display_name varchar(30) not null
);
