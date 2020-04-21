package br.com.ztech.backendchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movies")
public class Movie implements Serializable {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private LocalDate releaseDate;

    private CensorshipLevel censorshipLevel;

    private String director;

    private List<Cast> cast;

    @CreatedDate
    private LocalDateTime createdDate;

}
