package ru.otus.hw14.models.mongo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import ru.otus.hw14.models.h2.AuthorH2;

@NoArgsConstructor
@Getter
@ToString
@Document("authors")
public class AuthorMongo {
    @Id
    String id;
    @Field("firstName")
    private String firstName;
    @Field("middleName")
    private String middleName;
    @Field("lastName")
    private String lastName;
    @Field("h2Id")
    private long h2Id;


    public AuthorMongo(String id, String firstName, String middleName, String lastName, long h2Id) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.h2Id = h2Id;
    }

    public AuthorMongo(String firstName, String middleName, String lastName, long h2Id) {
        this(null, firstName, middleName, lastName, h2Id);
    }
    public AuthorMongo(String firstName, String middleName, String lastName) {
        this(null, firstName, middleName, lastName, 0L);
    }

    public static AuthorMongo fromH2(AuthorH2 authorH2) {
        return new AuthorMongo(authorH2.getFirstName(), authorH2.getMiddleName(), authorH2.getLastName(), authorH2.getId());
    }

}
