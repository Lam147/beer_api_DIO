package one.digitalinnovation.beerstock.entity;

import lombok.*;
import one.digitalinnovation.beerstock.enums.BeerType; 
import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Beer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String name;
    @Column(nullable = false) private String brand;
    @Column(nullable = false) private int max;
    @Column(nullable = false) private int quantity;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private BeerType type;
}
