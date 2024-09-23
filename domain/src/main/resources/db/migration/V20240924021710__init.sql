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

create table if not exists ticker_symbol (
    id bigint primary key auto_increment,
    symbol varchar(30) not null,
    name varchar(30) not null,
    api_provider varchar(30) not null,
    created_at timestamp not null default current_timestamp
);

insert into ticker_symbol (symbol, name, api_provider) values ('SOL', 'Solana', 'UPBIT');
insert into ticker_symbol (symbol, name, api_provider) values ('BTC', 'Bitcoin', 'UPBIT');
insert into ticker_symbol (symbol, name, api_provider) values ('ETH', 'Ethereum', 'UPBIT');
insert into ticker_symbol (symbol, name, api_provider) values ('USDT', 'Tether USDt', 'UPBIT');