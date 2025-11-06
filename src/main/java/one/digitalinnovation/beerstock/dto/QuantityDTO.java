package one.digitalinnovation.beerstock.dto;

import lombok.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class QuantityDTO {
    @NotNull @Max(100) private Integer quantity;
}
