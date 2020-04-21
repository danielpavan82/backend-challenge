package br.com.ztech.backendchallenge.controller.response;

import br.com.ztech.backendchallenge.model.CensorshipLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse implements Serializable {

    private String id;

    private String name;

    private LocalDate releaseDate;

    private CensorshipLevel censorshipLevel;

    private String director;

    private List<CastResponse> cast;

    private LocalDateTime createdDate;

}
