package one.digitalinnovation.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BeerNotFoundException extends Exception {
    
    // Construtor para ID (Long)
    public BeerNotFoundException(Long id) {
        super(String.format("Beer with id %d not found in the system.", id));
    }
    
    // Construtor para Nome (String)
    public BeerNotFoundException(String name) {
        super(String.format("Beer with name %s not found in the system.", name));
    }
}
