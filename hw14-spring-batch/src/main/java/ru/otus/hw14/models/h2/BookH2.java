package ru.otus.hw14.models.h2;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
@NoArgsConstructor
@Getter
@Entity
@Table(name = "books")
public class BookH2 {
    @Id
    @Column(name = "id")
    private long id;
    private long authorId;
    @Column(name = "title")
    private String title;
    @Column(name = "short_content")
    private String shortContent;


    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "books_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<GenreH2> genres;


    @Override
    public String toString() {
        return "BookH2{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", shortContent='" + shortContent + '\'' +
                '}';
    }


}
