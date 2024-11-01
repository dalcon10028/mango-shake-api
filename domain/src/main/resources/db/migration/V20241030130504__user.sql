create table if not exists users (
    uid serial primary key,
    auth_provider varchar(20) not null,
    username varchar(200) not null,
    nickname varchar(200) not null,
    profile_image_url varchar(255) null,
    role varchar(20) not null,
    created_at timestamp not null default current_timestamp
);