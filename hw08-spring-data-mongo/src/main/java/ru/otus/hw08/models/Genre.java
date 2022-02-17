package ru.otus.hw08.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@NoArgsConstructor
@Getter
@ToString
@Document("genres")
public class Genre {
    @Id
    private String id;
    @Field("name")
    private String name;

    public Genre(String id, String name) {
        this.id = id;
        this.name = name == null ? "" : name.trim();
    }

    public Genre(String name) {
        this(null, name);
    }



}
