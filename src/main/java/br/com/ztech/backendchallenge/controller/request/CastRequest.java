package br.com.ztech.backendchallenge.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CastRequest implements Serializable {

    @NotBlank(message = "Cast name cannot be blank")
    @NotNull(message = "Cast name is required")
    private String name;

}
