package one.digitalinnovation.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerStockExceededException extends Exception {
    public BeerStockExceededException(Long id, int quantity) {
        super(String.format("Beer stock exceeded the maximum capacity or resulted in a negative value for ID %d. Current change: %d", id, quantity));
    }
}
