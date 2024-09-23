create table if not exists ohlcv_day (
    id bigint primary key auto_increment,
    base_date date not null,
    exchange varchar(10) not null,
    symbol varchar(30) not null,
    open decimal(32, 8) not null,
    high decimal(32, 8) not null,
    low decimal(32, 8) not null,
    close decimal(32, 8) not null,
    volume decimal(32, 8) not null,
    created_at timestamp not null default current_timestamp
);