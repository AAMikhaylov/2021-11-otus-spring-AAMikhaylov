package ru.otus.hw07.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "genres")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name", nullable = false)
    private String name;

    public Genre(long id, String name) {
        this.id = id;
        this.name = name == null ? "" : name.trim();
    }

    public Genre(String name) {
        this(0L, name);
    }


}
