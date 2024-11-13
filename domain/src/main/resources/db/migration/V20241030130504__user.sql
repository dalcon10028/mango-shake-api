create table if not exists users (
    uid serial primary key,
    auth_provider varchar(20) not null,
    username varchar(200) not null,
    nickname varchar(200) not null,
    profile_image_url varchar(255) null,
    role varchar(20) not null,
    created_at timestamp not null default current_timestamp
);


create table if not exists order_status_upbit (
    id serial primary key,
    wallet_id bigint not null,
    uuid varchar(255) not null unique,
    side varchar(20) not null,
    order_type varchar(20) not null,
    price numeric null,
    status varchar(20) not null,
    market varchar(20) not null,
    ordered_at timestamp not null,
    volume numeric null,
    remaining_volume numeric null,
    reserved_fee numeric not null,
    remaining_fee numeric not null,
    paid_fee numeric not null,
    locked numeric not null,
    executed_volume numeric not null,
    executed_amount numeric not null,
    trades_count int not null,
    time_in_force varchar(20) null,
    updated_at timestamp not null default current_timestamp,
    created_at timestamp not null default current_timestamp
    );

create index order_status_upbit_wallet_id_index
    on order_status_upbit (wallet_id);

create table if not exists order_status_upbit_history (
  id serial primary key,
  order_status_upbit_id bigint not null,
  wallet_id bigint not null,
  status varchar(20) not null,
    volume numeric null,
    remaining_volume numeric null,
    reserved_fee numeric not null,
    remaining_fee numeric not null,
    paid_fee numeric not null,
    locked numeric not null,
    executed_volume numeric not null,
    executed_amount numeric not null,
    trades_count int not null,
    time_in_force varchar(20) null,
    created_at timestamp not null default current_timestamp
    );

create index order_status_upbit_history_order_status_upbit_id_index
    on order_status_upbit_history (order_status_upbit_id);

create index order_status_upbit_history_wallet_id_index
    on order_status_upbit_history (wallet_id);

create or replace function order_status_upbit_history_trigger_function()
    returns trigger as
$$
begin
insert into order_status_upbit_history (
    order_status_upbit_id,
    wallet_id,
    status,
    volume,
    remaining_volume,
    reserved_fee,
    remaining_fee,
    paid_fee,
    locked,
    executed_volume,
    executed_amount,
    trades_count,
    time_in_force
) values (
             new.id,
             new.wallet_id,
             new.status,
             new.volume,
             new.remaining_volume,
             new.reserved_fee,
             new.remaining_fee,
             new.paid_fee,
             new.locked,
             new.executed_volume,
             new.executed_amount,
             new.trades_count,
             new.time_in_force
         );
return new;
end;
$$
language plpgsql;


create trigger order_status_upbit_history_trigger
    after insert or update on order_status_upbit
                        for each row
                        execute function order_status_upbit_history_trigger_function();