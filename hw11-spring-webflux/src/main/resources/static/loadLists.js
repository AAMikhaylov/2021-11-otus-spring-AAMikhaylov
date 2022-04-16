function loadAuthors() {
    fetch("/api/authors")
        .then(function (response) {
            if (!response.ok) {
                response.json()
                    .then(jsonError => showGlobalError(jsonError));
                return;
            }
            response.json()
                .then(
                    function (authors) {
                        let content = "";
                        authors.forEach(function (author) {
                            content = content.concat('<div class="listElem">',
                                '<table class="listElem">',
                                '<tr>',
                                '<th>ID:</th>',
                                '<td>',
                                '<a class="contextHref" href="/author?id=' + author.id + '">' + author.id + '</a>',
                                '</td>',
                                '</tr>',
                                '<tr>',
                                '<th>Название:</th>',
                                '<td>',
                                '<a class="contextHref" href="/author?id=' + author.id + '">',
                                author.fullName,
                                '</a>',
                                '</td>',
                                '</tr>',
                                '</table></div>'
                            );
                        });
                        contentListDiv.innerHTML = content;
                    });
        });
}

function loadGenres() {
    fetch("/api/genres")
        .then(function (response) {
            if (!response.ok) {
                response.json()
                    .then(jsonError => showGlobalError(jsonError));
                return;
            }
            response.json()
                .then(
                    function (genres) {
                        let content = "";
                        genres.forEach(function (genre) {
                            content = content.concat('<div class="listElem">',
                                '<table class="listElem">',
                                '<tr>',
                                '<th>ID:</th>',
                                '<td>',
                                '<a class="contextHref" href="/genre?id=' + genre.id + '">' + genre.id + '</a>',
                                '</td>',
                                '</tr>',
                                '<tr>',
                                '<th>Название:</th>',
                                '<td>',
                                '<a class="contextHref" href="/genre?id=' + genre.id + '">',
                                genre.name,
                                '</a>',
                                '</td>',
                                '</tr>',
                                '</table></div>'
                            );
                        });
                        contentListDiv.innerHTML = content;
                    });
        });
}

function loadBookComments(url) {
    fetch(url)
        .then(function (response) {
            if (!response.ok) {
                response.json()
                    .then(jsonError => showGlobalError(jsonError));
                return;
            }
            response.json()
                .then(
                    function (comments) {
                        let content = "";
                        comments.forEach(function (comment) {
                            content = content.concat('<div class="listElem">',
                                '<table class="listElem">',
                                '<tr>',
                                '<th>ID:</th>',
                                '<td>',
                                '<a class="contextHref" href="/comment?id=' + comment.id + '">' + comment.id + '</a>',
                                '</td>',
                                '</tr>',
                                '<tr>',
                                '<th>Пользователь:</th>',
                                '<td>' + comment.userName + '</td>',
                                '</tr>',
                                '<tr>',
                                '<th>Дата:</th>',
                                '<td>' + comment.commentDate + '</td>',
                                '</tr>',
                                '<tr>',
                                '<th style="vertical-align: center;">Комментарий:</th>',
                                '<td class="commentText">' + comment.content + '</td>',
                                '</tr>',
                                '</table></div>'
                            );
                        });
                        contentListDiv.innerHTML = content;
                    });
        });
}

function loadComments(url) {
    fetch(url)
        .then(function (response) {
            if (!response.ok) {
                response.json()
                    .then(jsonError => showGlobalError(jsonError));
                return;
            }
            response.json()
                .then(
                    function (comments) {
                        let content = "";
                        comments.forEach(function (comment) {
                            content = content.concat('<div class="listElem">',
                                '<table class="listElem">',
                                '<tr>',
                                '<th>ID:</th>',
                                '<td>',
                                '<a class="contextHref" href="/comment?id=' + comment.id + '">' + comment.id + '</a>',
                                '</td>',
                                '</tr>',
                                '<tr>',
                                '<th>Пользователь:</th>',
                                '<td>' + comment.userName + '</td>',
                                '</tr>',
                                '<tr>',
                                '<th>Дата:</th>',
                                '<td>' + comment.commentDate + '</td>',
                                '</tr>',
                                '<tr>',
                                '<th>Книга:</th>',
                                '<td>');
                            if (comment.book != null) {
                                content = content.concat('<a class="contextHref" href="/book?id=' + comment.book.id + '">',
                                    comment.book.author.shortName + ' "' + comment.book.title + '"</a>');
                            }
                            content = content.concat('</td>',
                                '</tr>',
                                '<tr>',
                                '<th style="vertical-align: center;">Комментарий:</th>',
                                '<td class="commentText">' + comment.content + '</td>',
                                '</tr>',
                                '</table></div>'
                            );
                        });
                        contentListDiv.innerHTML = content;
                    });
        });
}

function loadBooks() {
    fetch("/api/books")
        .then(function (response) {
            if (!response.ok) {
                response.json()
                    .then(jsonError => showGlobalError(jsonError));
                return;
            }
            response.json()
                .then(
                    function (books) {
                        let content = "";
                        books.forEach(function (book) {
                            content = content.concat('<div class="listElem">',
                                '<table class="listElem">',
                                '<tr>',
                                '<th>ID:</th>',
                                '<td>',
                                '<a class="contextHref" href="/book?id=' + book.id + '">' + book.id + '</a>',
                                '</td>',
                                '</tr>',
                                '<tr>',
                                '<th>Автор:</th>',
                                '<td>',
                                '<a class="contextHref" href="/author?id=' + book.author.id + '">',
                                book.author.shortName,
                                '</a>',
                                '</td>',
                                '</tr>',
                                '<tr>',
                                '<th>Название:</th>',
                                '<td>',
                                '<a class="contextHref" href="/book?id=' + book.id + '">"' + book.title + '"</a>',
                                '</td>',
                                '</tr>',
                                '<tr>',
                                '<th>Жанр(ы):</th>',
                                '<td>',
                                getGenresList(book.genres),
                                '</td>',
                                '</tr>',
                                '</table></div>'
                            );
                        });
                        contentListDiv.innerHTML = content;
                    });
        });
}

function getGenresList(genres) {
    let result = "";
    for (i = 0; i < genres.length; i++) {
        result = result.concat('<span style="margin: 0; padding: 0">',
            '<a class="contextHref" href="genre?id=' + genres[i].id + '">');
        if (i === genres.length - 1)
            result = result.concat(genres[i].name);
        else
            result = result.concat(genres[i].name, ', ');
        result = result.concat('</a></span>');
    }
    return result;
}

function loadSelectAuthor(selectedId) {
    let selectField = document.getElementById("book.author");
    if (selectedId == 0)
        selectField.options[0].selected = true;
    fetch("/api/authors")
        .then(function (response) {
            if (!response.ok) {
                response.json()
                    .then(jsonError => showGlobalError(jsonError));
                return;
            }
            response.json()
                .then(
                    function (authors) {
                        authors.forEach(function (author) {
                            let opt = document.createElement('option');
                            opt.value = author.id;
                            opt.innerHTML = author.shortName;
                            opt.selected = (author.id == selectedId);
                            selectField.appendChild(opt);
                        });
                    });
        });
}


function loadCheckBoxGenres(checkedGenres) {
    let checkedIds = [];
    if (checkedGenres != null)
        checkedIds = checkedGenres.map(g => g.id);
    fetch("/api/genres")
        .then(function (response) {
            if (!response.ok) {
                response.json()
                    .then(jsonError => showGlobalError(jsonError));
                return;
            }
            let content = "";
            response.json()
                .then(
                    function (genres) {
                        genres.forEach(function (genre) {
                            let checked = '';
                            if (checkedIds.includes(genre.id))
                                checked = ' checked';
                            content = content.concat('<div style="width: 230px;float: left">',
                                '<input type="checkbox" name="genres" id="ids-genre' + genre.id + '" value="' + genre.id + '"' + checked + '>',
                                '<label for="ids-genre' + genre.id + '">',
                                genre.name,
                                '</label>',
                                '</div>');
                        });
                        content = content.concat('<ul class="error" id="book.genresErrors"></ul>');
                        genresCell.innerHTML = content;
                    });
        });
}
