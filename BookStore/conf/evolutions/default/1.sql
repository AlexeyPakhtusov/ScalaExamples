# --- Books database schema

# --- !Ups

set ignorecase true;

create table book (
  id bigint not null,
  title varchar(255) not null,
  price int not null,
  author varchar(255) not null,
  constraint pk_book primary key (id),
  constraint ch_book_price check(price > 0)
);

create sequence book_seq start with 100;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists book;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists book_seq;
