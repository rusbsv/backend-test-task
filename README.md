Три таблицы: книги, авторы, книги-авторы (для связи многие-ко-многим)


create table books (
bookId int auto_increment not null primary key,
title varchar(120) not null,
year int);

create table authors (
authorId int auto_increment not null primary key,
authorName varchar(120) not null
);

create table booksByAuthors (
bookId int not null,
authorId int not null,
FOREIGN KEY (bookId) REFERENCES books (bookId) ON DELETE RESTRICT ON UPDATE CASCADE,
FOREIGN KEY (bookId) REFERENCES authors (authorId) ON DELETE RESTRICT ON UPDATE CASCADE,
PRIMARY KEY (bookId, authorId)
)