insert into GENRES (NAME)
values ('Фантастика');
insert into GENRES (NAME)
values ('Приключения');
insert into GENRES (NAME)
values ('Путешествия');
insert into GENRES (NAME)
values ('Сказки');
insert into GENRES (NAME)
values ('Роман');
insert into GENRES (NAME)
values ('Современные детективы');
insert into GENRES (NAME)
values ('Зарубежная фантастика');
insert into GENRES (NAME)
values ('Космическая фантастика');
insert into GENRES (NAME)
values ('Без жанра');

insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Александр','Сергеевич','Пушкин');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Михаил','Афанасьевич','Булгаков');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Антон','Павлович','Чехов');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Татьяна','Витальевна','Устинова');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Франклин','Патрик','Герберт');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Александр','Иванович','Кузнецов');

insert into BOOKS (author_id, title, short_content)
values (2, 'Мастер и Маргарита', 'Краткое содержание книги Мастер и Маргарита...');
insert into BOOKS (author_id, title, short_content)
values (4, 'Судьба по книге перемен', 'Детективная история...');
insert into BOOKS (author_id, title, short_content)
values (1, 'Евгений Онегин', 'Краткое содержание романа...');
insert into BOOKS (author_id, title, short_content)
values (5, 'Дюна', 'Действие «Дюны» происходит в далеком будущем – через 20 тысяч лет от условно нашего времени....');
insert into BOOKS (author_id, title, short_content)
values (6, 'Арктическая одиссея',
        'В основе остросюжетной книги «Арктическая одиссея» – дневник полярного Робинзона 20-летнего Александра Кузнецова');



insert into BOOKS_GENRES (book_id, genre_id)
values (1, 1);
insert into BOOKS_GENRES (book_id, genre_id)
values (1, 2);
insert into BOOKS_GENRES (book_id, genre_id)
values (2, 6);
insert into BOOKS_GENRES (book_id, genre_id)
values (3, 5);
insert into BOOKS_GENRES (book_id, genre_id)
values (4, 7);
insert into BOOKS_GENRES (book_id, genre_id)
values (4, 8);
insert into BOOKS_GENRES (book_id, genre_id)
values (5, 2);


