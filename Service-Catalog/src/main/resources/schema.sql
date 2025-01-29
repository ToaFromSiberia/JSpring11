-- drop table if exists products;
-- drop table if exists blocked_products;

create table if not exists products (
    id bigint generated always as identity,
    name varchar(255) not null,
    price decimal(10,2) not null,
    stock int not null,
    description varchar(2048),
    image_file varchar(255),
    primary key (id)
);

create table blocked_products (
    order_id uuid,
    product_id bigint not null,
    quantity int not null,
    primary key (order_id)
);