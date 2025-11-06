package one.digitalinnovation.beerstock.dto;

import lombok.*;
import one.digitalinnovation.beerstock.enums.BeerType; 
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class BeerDTO {
    private Long id;
    @NotNull @Size(min = 1, max = 200) private String name;
    @NotNull @Size(min = 1, max = 200) private String brand;
    @NotNull @Max(500) private Integer max;
    @NotNull @Max(100) private Integer quantity;
    @NotNull private BeerType type;
}
