insert into GENRES (NAME)
values ('genre1');
insert into GENRES (NAME)
values ('genre2');
insert into GENRES (NAME)
values ('genre3');

insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('author11','author12','author13');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('author21','author22','author23');
insert into AUTHORS (FIRST_NAME, MIDDLE_NAME, LAST_NAME)
values ('author31','author32','author33');

insert into BOOKS (author_id, title, short_content)
values (2, 'bookTitle1', 'bookShortContent1');
insert into BOOKS (author_id, title, short_content)
values (1, 'bookTitle2', 'bookShortContent2');
insert into BOOKS (author_id, title, short_content)
values (3, 'bookTitle3', 'bookShortContent3');

insert into BOOKS_GENRES (book_id, genre_id)
values (1, 1);
 insert into BOOKS_GENRES (book_id, genre_id)
 values (2, 1);
insert into BOOKS_GENRES (book_id, genre_id)
values (2, 2);
insert into BOOKS_GENRES (book_id, genre_id)
values (3, 2);

insert into COMMENTS(book_id, user_name, content)
values (1, 'User1', 'CommentBook11');
insert into COMMENTS(book_id, user_name, content)
values (2, 'User2', 'CommentBook21');
insert into COMMENTS(book_id, user_name, content)
values (2, 'User3', 'CommentBook22');


