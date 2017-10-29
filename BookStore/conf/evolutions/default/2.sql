# --- Sample books dataset

# --- !Ups

insert into book(id, title, price, author) values (1, 'C++', 30, 'Bjarne Stroustrup');
insert into book(id, title, price, author) values (2, 'Java', 25, 'James Gosling');
insert into book(id, title, price, author) values (3, 'Scala', 25, 'Martin Odersky');

# --- !Downs

delete from book
