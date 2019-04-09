#### Постановка задачи

Написать простейшие CRUD приложение для реализации библиотеки.

Через POST метод добавляется книга.

Пример: { title: "Дэвид Копперфильд", year: 1850, authors: ["Чарльз Диккенс"]}

Авторов у книги может быть больше 1. Если попадается новый автор он заносится в базу в отдельную таблицу авторов. Со стороны баз данных надо организовать связь авторов и книг посредством связи многие ко многим.

Добавить GET методы для получения списков книг/авторов.

Реализовать методы по удалению / обновлению книг.

Будет плюсом реализовать фронтовую часть и его взаимодействие с бэком.

#### Технологии

Язык – Scala

Framework – Play

Система сборки - sbt

База – MySql или любая другая СУБД



# Запросы для создания таблиц

#### Три таблицы: books, authors, books_by_authors (для связи многие-ко-многим):


create table books (

book_id int auto_increment not null primary key, 

title varchar(120) not null,

year int

);


create table authors (

author_id int auto_increment not null primary key,

author_name varchar(120) not null

);


create table books_by_authors (

book_id int not null,

author_id int not null,

constraint books_fk

foreign key (book_id) references books (book_id) on delete cascade on update cascade,

constraint authors_fk

foreign key (book_id) references authors (author_id) on delete cascade on update cascade

);
