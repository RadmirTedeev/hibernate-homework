drop table if exists customers cascade; 
drop table if exists products cascade; 
drop table if exists orders cascade;

create table customers (id serial primary key, name text not null);
create table products (id serial primary key, title text not null, price double precision not null);
create table orders (
--  	id serial primary key, 
 	customer_id int not null references customers (id) on delete cascade,
 	product_id int not null references products (id) on delete cascade,
 	price double precision not null
);