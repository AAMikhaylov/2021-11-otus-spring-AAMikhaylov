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
values ('Александр', 'Сергеевич', 'Пушкин');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Михаил', 'Афанасьевич', 'Булгаков');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Антон', 'Павлович', 'Чехов');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Татьяна', 'Витальевна', 'Устинова');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Франклин', 'Патрик', 'Герберт');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('Александр', 'Иванович', 'Кузнецов');

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

insert into COMMENTS(book_id, user_name, content)
values (1, 'User1', 'Комментарий 1 к книге "Мастер и Маргарита"');
insert into COMMENTS(book_id, user_name, content)
values (1, 'User2','Если бы меня, как водится, спросили о единственной книге, которую я взяла бы на необитаемый остров,
это, безусловно, была бы эта книга – книга-шедевр, книга-дыхание, книга-сказка. Это история и любви,
 и веры, и прощения. Это драма, которая местами комедия. Юмор Булгакова – это мой юмор, его слова –
это слова из моей песни. Хитросплетение сюжетов, прекрасные параллели между ними, уморительные диа-
логи. Герои, которые поражают.
Мне нравятся практически все герои этого романа. Они дополняют друг друга, они невозможны друг без
друга. Единственный, кто всегда вызывал непонимание, раздражение, злость – это сам Мастер, человек,
которому дан талант и который ничего не делает, чтобы этот талант защитить. А безусловный любимчик
– это кот Бегемот.
О чудесной идее романа можно говорить бесконечно. Можно, но не нужно, потому что эту книгу нужно
читать. Сначала быстро, за два дня, а потом еще раз, уже смакуя, снова и снова возвращаясь к одним
и тем же строкам, представляя на себе белый плащ с кровавым подбоем, чувствуя, как в твою кожу вти-
рается крем, пахнущий болотной тиной, или раскачиваясь на люстре с примусом в лапах…
');
insert into COMMENTS(book_id, user_name, content)
values (1, 'User3', 'Комментарий 3 к книге "Мастер и Маргарита"');
insert into COMMENTS(book_id, user_name, content)
values (1, 'User4', 'Комментарий 4 к книге "Мастер и Маргарита"');
insert into COMMENTS(book_id, user_name, content)
values (1, 'User5', 'Комментарий 5 к книге "Мастер и Маргарита"');
insert into COMMENTS(book_id, user_name, content)
values (1, 'User6', 'Комментарий 6 к книге "Мастер и Маргарита"');
insert into COMMENTS(book_id, user_name, content)
values (1, 'User7', 'Комментарий 7 к книге "Мастер и Маргарита"');
insert into COMMENTS(book_id, user_name, content)
values (1, 'User8', 'Комментарий 8 к книге "Мастер и Маргарита"');
insert into COMMENTS(book_id, user_name, content)
values (1, 'User9', 'Комментарий 9 к книге "Мастер и Маргарита"');
insert into COMMENTS(book_id, user_name, content)
values (1, 'User10', 'Комментарий 10 к книге "Мастер и Маргарита"');
insert into COMMENTS(book_id, user_name, content)
values (1, 'User11', 'Комментарий 11 к книге "Мастер и Маргарита"');
insert into COMMENTS(book_id, user_name, content)
values (1, 'User12', 'Комментарий 12 к книге "Мастер и Маргарита"');


insert into COMMENTS(book_id, user_name, content)
values (2, 'User13', 'Комментарий 1 к книге "Судьба по книге перемен"');
insert into COMMENTS(book_id, user_name, content)
values (2, 'User14', 'Комментарий 2 к книге "Судьба по книге перемен"');
insert into COMMENTS(book_id, user_name, content)
values (2, 'User15', 'Комментарий 3 к книге "Судьба по книге перемен"');
insert into COMMENTS(book_id, user_name, content)
values (2, 'User16', 'Комментарий 4 к книге "Судьба по книге перемен"');
insert into COMMENTS(book_id, user_name, content)
values (2, 'User17', 'Комментарий 5 к книге "Судьба по книге перемен"');


insert into COMMENTS(book_id, user_name, content)
values (3, 'User18', 'Комментарий 1 к книге "Евгений Онегин"');
insert into COMMENTS(book_id, user_name, content)
values (3, 'User19', 'Комментарий 2 к книге "Евгений Онегин"');
insert into COMMENTS(book_id, user_name, content)
values (3, 'User20', 'Комментарий 3 к книге "Евгений Онегин"');

insert into COMMENTS(book_id, user_name, content)
values (4, 'User21', 'Комментарий 1 к книге "Дюна"');
insert into COMMENTS(book_id, user_name, content)
values (4, 'User22', 'Комментарий 2 к книге "Дюна"');

insert into COMMENTS(book_id, user_name, content)
values (5, 'User23', 'Комментарий 1 к книге "Арктическая одиссеяАрктическая одиссея"');


/* password=pass */
insert into USERS(name, password, enabled, locked, expired)
values ('admin', '$2a$12$3lW5c8GeZAnnZYfJyGYcnOt6CPheL.uD7K1Kr334I/Sa.HLMLw2Jq', true, false, false);
insert into USERS(name, password, enabled, locked, expired)
values ('user', '$2a$12$3lW5c8GeZAnnZYfJyGYcnOt6CPheL.uD7K1Kr334I/Sa.HLMLw2Jq', true, false, false);
insert into ROLES(name)
values ('ROLE_ADMIN');
insert into ROLES(name)
values ('ROLE_USER');
insert into USERS_ROLES (user_id, role_id)
values (1, 1);
insert into USERS_ROLES (user_id, role_id)
values (2, 2);

