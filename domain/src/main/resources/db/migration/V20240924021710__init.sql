create table if not exists ohlcv_day (
    id serial primary key,
    base_date date not null,
    exchange varchar(10) not null,
    currency varchar(10) not null,
    symbol varchar(30) not null,
    open decimal(32, 8) not null,
    high decimal(32, 8) not null,
    low decimal(32, 8) not null,
    close decimal(32, 8) not null,
    volume decimal(32, 8) not null,
    created_at timestamp not null default current_timestamp
);

create table if not exists ticker_symbol (
    id serial primary key,
    symbol varchar(30) not null,
    base_currency varchar(10) not null default 'KRW',
    name varchar(30) not null,
    market varchar(30) not null,
    created_at timestamp not null default current_timestamp
);

insert into ticker_symbol (symbol, base_currency, name, market) values ('SOL', 'KRW', 'Solana', 'CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('BTC', 'KRW', 'Bitcoin', 'CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('ETH', 'KRW', 'Ethereum', 'CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('USDT', 'KRW', 'Tether USDt', 'CRYPTO_CURRENCY');

create table if not exists wallet (
    id serial primary key,
    api_provider varchar(30) not null,
    status varchar(30) not null default 'ACTIVE',
    app_key varchar(255) not null,
    app_secret varchar(255) not null,
    additional_info jsonb not null,
    memo text,
    last_synced_at timestamp null default current_timestamp,
    created_at timestamp not null default current_timestamp
);

create table if not exists wallet_security (
    id serial primary key,
    wallet_id int not null,
    currency varchar(10) not null,
    symbol varchar(30) not null,
    balance decimal(32, 8) not null,
    locked decimal(32, 8) not null,
    average_buy_price decimal(32, 8) not null,
    last_synced_at timestamp null default current_timestamp,
    created_at timestamp not null default current_timestamp
);