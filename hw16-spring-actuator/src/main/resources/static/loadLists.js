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
                    function (jsonResp) {
                        let content = "";
                        jsonResp._embedded.authors.forEach(function (author) {
                            content = content.concat('<div class="listElem">',
                                '<table class="listElem">',
                                '<tr>',
                                '<th>ФИО:</th>',
                                '<td>',
                                '<a class="contextHref" href="/author?url=' + encodeURIComponent(author._links.self.href) + '">',
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
                    function (jsonResp) {
                        let content = "";
                        jsonResp._embedded.genres.forEach(function (genre) {
                            content = content.concat('<div class="listElem">',
                                '<table class="listElem">',
                                '<tr>',
                                '<th>Название:</th>',
                                '<td>',
                                '<a class="contextHref" href="/genre?url=' + encodeURIComponent(genre._links.self.href) + '">',
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
                    function (jsonResp) {
                        let content = "";
                        jsonResp._embedded.comments.forEach(function (comment) {
                            content = content.concat('<div class="listElem">',
                                '<table class="listElem">',
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
                                '<td class="commentText">',
                                '<a class="contextHref" href="/comment?url=' + encodeURIComponent(comment._links.self.href) + '">',
                                comment.content,
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

function loadComments() {
    fetch("/api/comments")
        .then(function (response) {
            if (!response.ok) {
                response.json()
                    .then(jsonError => showGlobalError(jsonError));
                return;
            }
            response.json()
                .then(
                    function (jsonResp) {
                        let content = "";
                        jsonResp._embedded.comments.forEach(function (comment) {
                            content = content.concat('<div class="listElem">',
                                '<table class="listElem">',
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
                            let book = comment._embedded.book;
                            content = content.concat('<a class="contextHref"',
                                'href="/book?url=' + encodeURIComponent(book._links.self.href.replace('{?projection}', '')) + '">',
                                book.author.shortName + ' "' + book.title + '"</a>',
                                '</td>',
                                '</tr>',
                                '<tr>',
                                '<th style="vertical-align: center;">Комментарий:</th>',
                                '<td class="commentText">',
                                '<a class="contextHref" href="/comment?url=' + encodeURIComponent(comment._links.self.href) + '">',
                                comment.content,
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
                    function (jsonResp) {
                        let content = "";
                        jsonResp._embedded.books.forEach(function (book) {
                            content = content.concat('<div class="listElem">',
                                '<table class="listElem">',
                                '<tr>',
                                '<th>Автор:</th>',
                                '<td>',
                                '<a class="contextHref" href="/author?url=',
                                encodeURIComponent(book.author._links.self.href.replace('{?projection}', '')),
                                '">',
                                book.author.shortName,
                                '</a>',
                                '</td>',
                                '</tr>',
                                '<tr>',
                                '<th>Название:</th>',
                                '<td>',
                                '<a class="contextHref" href="/book?url=' + encodeURIComponent(book._links.self.href) + '">"' + book.title + '"</a>',
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
            '<a class="contextHref" href="genre?url=' + encodeURIComponent(genres[i]._links.self.href.replace('{?projection}', '')) + '">');
        if (i === genres.length - 1)
            result = result.concat(genres[i].name);
        else
            result = result.concat(genres[i].name, ', ');
        result = result.concat('</a></span>');
    }
    return result;
}

function loadSelectAuthor(selectedAuthorUrl) {
    let selectField = document.getElementById("book.author");
    if (selectedAuthorUrl === "")
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
                    function (jsonResp) {
                        jsonResp._embedded.authors.forEach(function (author) {
                            let opt = document.createElement('option');
                            opt.value = author._links.self.href;
                            opt.innerHTML = author.shortName;
                            opt.selected = (author._links.self.href === selectedAuthorUrl);
                            selectField.appendChild(opt);
                        });
                    });
        });
}


function loadCheckBoxGenres(checkedGenres) {
    let checkedGenreUrls = [];
    if (checkedGenres != null)
        checkedGenreUrls = checkedGenres.map(g => g._links.self.href.replace('{?projection}', ''));
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
                    function (jsonResp) {
                        let idx = 0;
                        jsonResp._embedded.genres.forEach(function (genre) {
                            let checked = '';
                            if (checkedGenreUrls.includes(genre._links.self.href.replace('{?projection}', '')))
                                checked = ' checked';
                            content = content.concat('<div style="width: 230px;float: left">',
                                '<input type="checkbox" name="genres" id="ids-genre' + idx + '"',
                                'value="' + genre._links.self.href.replace('{?projection}', ''),
                                '"' + checked + '>',
                                '<label for="ids-genre' + idx + '">',
                                genre.name,
                                '</label>',
                                '</div>');
                            idx++;
                        });
                        content = content.concat('<ul class="error" id="book.genresErrors"></ul>');
                        genresCell.innerHTML = content;
                    });
        });
}
