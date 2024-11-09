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

insert into ticker_symbol (symbol, base_currency, name, market) values ('SOL','KRW','Solana','CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('BTC','KRW','Bitcoin','CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('ETH','KRW','Ethereum','CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('AVAX','KRW','Avalanche','CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('SUI','KRW','Sui','CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('DOGE','KRW','Dogecoin','CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('USDC','KRW','USDC','CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('XRP','KRW','Ripple','CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('TRX','KRW','TRON','CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('TON','KRW','Toncoin','CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('ADA','KRW','Cardano','CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('SHIB','KRW','Shiba' 'Inu,CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('LINK','KRW','Chainlink','CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('BCH','KRW','Bitcoin' 'Cash,CRYPTO_CURRENCY');
insert into ticker_symbol (symbol, base_currency, name, market) values ('DOT','KRW','Polkadot','CRYPTO_CURRENCY');

create table if not exists wallet (
    id serial primary key,
    uid int not null,
    api_provider varchar(30) not null,
    status varchar(30) not null default 'ACTIVE',
    app_key varchar(255) not null,
    app_secret varchar(255) not null,
    additional_info jsonb not null,
    memo text,
    beginning_assets decimal(32, 8) not null default 0,
    ending_assets decimal(32, 8) not null default 0,
    deposits_during_period decimal(32, 8) not null default 0,
    withdrawals_during_period decimal(32, 8) not null default 0,
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

create table if not exists wallet_snapshot (
    id serial primary key,
    uid int not null,
    wallet_id int not null,
    base_date date not null,
    beginning_assets decimal(32, 8) not null default 0,
    ending_assets decimal(32, 8) not null default 0,
    deposits_during_period decimal(32, 8) not null default 0,
    withdrawals_during_period decimal(32, 8) not null default 0,
    created_at timestamp not null default current_timestamp
);

create table if not exists wallet_security_snapshot (
    id serial primary key,
    wallet_snapshot_id int not null,
    wallet_id int not null,
    wallet_security_id int not null,
    base_date date not null,
    currency varchar(10) not null,
    symbol varchar(30) not null,
    balance decimal(32, 8) not null,
    locked decimal(32, 8) not null,
    average_buy_price decimal(32, 8) not null,
    created_at timestamp not null default current_timestamp
);