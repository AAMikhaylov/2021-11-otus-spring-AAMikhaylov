package ru.otus.hw05.domain;

import lombok.Data;

@Data
public class Author {
    private final long id;
    private final String firstName;
    private final String middleName;
    private final String lastName;

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
