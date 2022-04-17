package ru.otus.hw12.dto;

import lombok.Getter;
import lombok.ToString;
import ru.otus.hw12.domain.Author;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@ToString
public class AuthorDto {
    private final Long id;
    @NotBlank(message = "Имя не должно быть быть пустым")
    @Size(min = 2, max = 25, message = "Длина имени должна быть от 2 до 25 символов")
    private final String firstName;
    @NotBlank(message = "Отчество не должно быть быть пустым")
    @Size(min = 2, max = 25, message = "Длина отчества должна быть от 2 до 25 символов")
    private final String middleName;
    @NotBlank(message = "Фамилия не должна быть быть пустой")
    @Size(min = 2, max = 25, message = "Длина фамилии должна быть от 2 до 25 символов")
    private final String lastName;

    public AuthorDto(Long id, String firstName, String middleName, String lastName) {
        this.id = (id == null ? 0L : id);
        this.firstName = firstName == null ? "" : firstName.trim();
        this.middleName = middleName == null ? "" : middleName.trim();
        this.lastName = lastName == null ? "" : lastName.trim();
    }


    public Author toEntity() {
        return new Author(id, firstName, middleName, lastName);
    }

    public static AuthorDto fromEntity(Author author) {
        return new AuthorDto(author.getId(), author.getFirstName(), author.getMiddleName(), author.getLastName());
    }


    public String getShortName() {
        StringBuilder sb = new StringBuilder();
        if (!firstName.isEmpty())
            sb.append(firstName.charAt(0)).append(".");
        if (!middleName.isEmpty())
            sb.append(middleName.charAt(0)).append(".");
        sb.append(" ").append(lastName);
        return sb.toString();
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder(firstName);
        if (!middleName.isEmpty())
            sb.append(" ").append(middleName);
        if (!lastName.isEmpty())
            sb.append(" ").append(lastName);
        return sb.toString();
    }


}
