package ru.otus.hw05.dao.ext;

import lombok.Data;

@Data
public class BookGenreRelation {
    private final long bookId;
    private final long genreId;
}
