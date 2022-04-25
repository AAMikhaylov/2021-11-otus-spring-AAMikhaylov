package ru.otus.hw14.models.h2;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "genres")
public class GenreH2 {
    @Id
    @Column(name = "id")
    private long id;
    @Column(name = "name")
    private String name;
}
