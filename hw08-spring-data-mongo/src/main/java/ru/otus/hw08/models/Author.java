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
@Document("authors")
public class Author {
    @Id
    String id;
    @Field("firstName")
    private String firstName;
    @Field("middleName")
    private String middleName;
    @Field("lastName")
    private String lastName;

    public Author(String id, String firstName, String middleName, String lastName) {
        this.id = id;
        this.firstName = firstName == null ? "" : firstName.trim();
        this.middleName = middleName == null ? "" : middleName.trim();
        this.lastName = lastName == null ? "" : lastName.trim();
    }

    public Author(String firstName, String middleName, String lastName) {
        this(null, firstName, middleName, lastName);
    }

    public String getShortName() {
        StringBuilder sb = new StringBuilder();
        if (!firstName.isEmpty())
            sb.append(firstName.charAt(0)).append(".");
        if (!middleName.isEmpty())
            sb.append(middleName.charAt(0)).append(".");
        sb.append(lastName);
        return sb.toString();
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder(lastName);
        if (!firstName.isEmpty())
            sb.append(" ").append(firstName);
        if (!middleName.isEmpty())
            sb.append(" ").append(middleName);
        return sb.toString();
    }
}
