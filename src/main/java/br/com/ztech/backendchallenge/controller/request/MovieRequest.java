package br.com.ztech.backendchallenge.controller.request;

import br.com.ztech.backendchallenge.model.CensorshipLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest implements Serializable {

    @NotBlank(message = "Name cannot be blank")
    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;

    @NotNull(message = "Censorship level is required. Allowed values: 'CENSURADO' and 'SEM_CENSURA'")
    private CensorshipLevel censorshipLevel;

    @NotBlank(message = "Director cannot be blank")
    @NotNull(message = "Director is required")
    private String director;

    @Size(min = 1, max = 10, message = "Cast must have at least {min} and at most {max} names")
    @NotNull(message = "Cast must have at least 1 and at most 10 names")
    private List<CastRequest> cast;

}
