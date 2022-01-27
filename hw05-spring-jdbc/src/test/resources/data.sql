insert into GENRES (NAME)
values ('Роман');
insert into GENRES (NAME)
values ('Фантастика');
insert into GENRES (NAME)
values ('Без жанра');

insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Александр','Сергеевич','Пушкин');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Франклин','Патрик','Герберт');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Александр','Иванович','Кузнецов');

insert into BOOKS (author_id, title, short_content)
values (1, 'Евгений Онегин', 'Краткое содержание романа...');
insert into BOOKS (author_id, title, short_content)
values (2, 'Дюна', 'Действие «Дюны» происходит в далеком будущем – через 20 тысяч лет от условно нашего времени....');

insert into BOOKS_GENRES (book_id, genre_id)
values (1, 1);
 insert into BOOKS_GENRES (book_id, genre_id)
 values (2, 1);
insert into BOOKS_GENRES (book_id, genre_id)
values (2, 2);
