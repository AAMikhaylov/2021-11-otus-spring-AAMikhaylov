package ru.otus.hw06.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "last_name", nullable = false)
    private String lastName;

    public Author(long id, String firstName, String middleName, String lastName) {
        this.id = id;
        this.firstName = firstName == null ? "" : firstName.trim();
        this.middleName = middleName == null ? "" : middleName.trim();
        this.lastName = lastName == null ? "" : lastName.trim();
    }

    public Author(String firstName, String middleName, String lastName) {
        this(0L, firstName, middleName, lastName);
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
