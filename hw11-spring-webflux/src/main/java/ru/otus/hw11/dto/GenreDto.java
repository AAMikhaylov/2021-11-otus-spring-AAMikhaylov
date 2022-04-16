package ru.otus.hw11.dto;
import lombok.Getter;
import ru.otus.hw11.domain.Genre;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class GenreDto {
    private final String id;
    @NotBlank(message = "Наименование не должно быть быть пустым")
    @Size(min = 2, max = 25, message = "Длина наименования должна быть от 2 до 25 символов")
    private final String name;

    public GenreDto(String id, String name) {
        this.id =id;
        this.name = name == null ? "" : name.trim();
    }

    public static GenreDto fromEntity(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }

    public Genre toEntity() {
        return new Genre(id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenreDto genreDto = (GenreDto) o;
        if (!id.equals(genreDto.id)) return false;
        return name.equals(genreDto.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}

