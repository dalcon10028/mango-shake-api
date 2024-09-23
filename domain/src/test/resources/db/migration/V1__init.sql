create table if not exists day_ohlcv (
    id serial primary key,
    base_date date not null,
    open decimal(32, 8) not null,
    high decimal(32, 8) not null,
    low decimal(32, 8) not null,
    close decimal(32, 8) not null,
    volume decimal(32, 8) not null,
    created_at timestamp not null default current_timestamp
);