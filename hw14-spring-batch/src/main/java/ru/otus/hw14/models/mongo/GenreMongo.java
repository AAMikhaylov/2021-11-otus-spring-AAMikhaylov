package ru.otus.hw14.models.mongo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import ru.otus.hw14.models.h2.GenreH2;

@NoArgsConstructor
@Getter
@ToString
@Document("genres")
public class GenreMongo {
    @Id
    private String id;
    @Field("name")
    private String name;
    @Field("h2Id")
    private long h2Id;

    public GenreMongo(String id, String name, long h2Id) {
        this.id = id;
        this.name = name;
        this.h2Id = h2Id;
    }

    public GenreMongo(String name, long h2Id) {
        this(null, name, h2Id);
    }

    public GenreMongo(String name) {
        this(null, name, 0);
    }

    public static GenreMongo fromH2(GenreH2 genreH2) {
        return new GenreMongo(genreH2.getName(), genreH2.getId());
    }

}
