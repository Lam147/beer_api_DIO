package one.digitalinnovation.beerstock.service;

import one.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.entity.Beer;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.exception.BeerStockExceededException;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito; // IMPORTANTE: Adiciona a importação da classe Mockito
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*; // Mantém importação estática

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    private static final long INVALID_BEER_ID = 1L;

    @Mock
    private BeerRepository beerRepository;

    @InjectMocks
    private BeerService beerService;

    private final BeerMapper beerMapper = BeerMapper.INSTANCE;

    // Métodos Auxiliares
    private BeerDTO createFakeDTO() {
        return BeerDTOBuilder.builder().build().toBeerDTO();
    }
    
    private Beer createFakeModel() {
        return beerMapper.toModel(createFakeDTO());
    }
    
    // --- TESTES DE CRIAÇÃO ---

    @Test
    void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
        // given
        BeerDTO expectedBeerDTO = createFakeDTO();
        Beer expectedSavedBeer = createFakeModel();

        // when
        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
        // CORREÇÃO: Uso explícito de Mockito.any() para resolver a ambiguidade.
        when(beerRepository.save(Mockito.any(Beer.class))).thenReturn(expectedSavedBeer); 

        // then
        BeerDTO createdBeerDTO = beerService.createBeer(expectedBeerDTO);

        assertThat(createdBeerDTO.getId(), is(equalTo(expectedBeerDTO.getId())));
        assertThat(createdBeerDTO.getName(), is(equalTo(expectedBeerDTO.getName())));
        assertThat(createdBeerDTO.getQuantity(), is(equalTo(expectedBeerDTO.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown() {
        // given
        BeerDTO expectedBeerDTO = createFakeDTO();
        Beer duplicatedBeer = createFakeModel();

        // when
        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

        // then
        assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(expectedBeerDTO));
    }
// RESTO DO CÓDIGO DA CLASSE BeerServiceTest.java (SEM ALTERAÇÕES)
// ... Certifique-se de que o restante do código da sua classe de teste está aqui!
// O código é o mesmo do meu post anterior, apenas a seção de import e o teste whenBeerInformedThenItShouldBeCreated foram alterados.
// Devido ao limite de espaço, você deve copiar o restante do código da sua versão anterior para completar este arquivo.

    @Test
    void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
        // given
        BeerDTO expectedBeerDTO = createFakeDTO();
        Beer expectedFoundBeer = createFakeModel();

        // when
        when(beerRepository.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));

        // then
        BeerDTO foundBeerDTO = beerService.findByName(expectedBeerDTO.getName());

        assertThat(foundBeerDTO, is(equalTo(expectedBeerDTO)));
    }

    @Test
    void whenNotRegisteredBeerNameIsGivenThenThrowAnException() {
        // given
        BeerDTO expectedBeerDTO = createFakeDTO();

        // when
        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());

        // then
        assertThrows(BeerNotFoundException.class, () -> beerService.findByName(expectedBeerDTO.getName()));
    }

    @Test
    void whenListBeerIsCalledThenReturnAListOfBeers() {
        // given
        BeerDTO expectedBeerDTO = createFakeDTO();
        Beer expectedFoundBeer = createFakeModel();

        // when
        when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));

        // then
        List<BeerDTO> foundListBeersDTO = beerService.listAll();

        assertThat(foundListBeersDTO, is(not(empty())));
        assertThat(foundListBeersDTO.get(0), is(equalTo(expectedBeerDTO)));
    }

    @Test
    void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {
        // when
        when(beerRepository.findAll()).thenReturn(Collections.emptyList());

        // then
        List<BeerDTO> foundListBeersDTO = beerService.listAll();

        assertThat(foundListBeersDTO, is(empty()));
    }

    // --- TESTES DE EXCLUSÃO ---

    @Test
    void whenValidBeerIdIsGivenThenBeerShouldBeDeleted() throws BeerNotFoundException {
        // given
        Beer expectedDeletedBeer = createFakeModel();

        // when
        when(beerRepository.findById(expectedDeletedBeer.getId())).thenReturn(Optional.of(expectedDeletedBeer));
        doNothing().when(beerRepository).deleteById(expectedDeletedBeer.getId());

        // then
        beerService.deleteById(expectedDeletedBeer.getId());

        verify(beerRepository, times(1)).findById(expectedDeletedBeer.getId());
        verify(beerRepository, times(1)).deleteById(expectedDeletedBeer.getId());
    }

    @Test
    void whenInvalidBeerIdIsGivenThenThrowException() {
        // when
        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        // then
        assertThrows(BeerNotFoundException.class, () -> beerService.deleteById(INVALID_BEER_ID));
    }

    // --- TESTES DE TDD (INCREMENTO) ---

    @Test
    void whenIncrementIsCalledThenIncrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
        // given
        BeerDTO expectedBeerDTO = createFakeDTO();
        Beer expectedBeer = createFakeModel();
        int incrementQuantity = 10;
        int expectedQuantityAfterIncrement = expectedBeer.getQuantity() + incrementQuantity;

        // when
        when(beerRepository.findById(expectedBeer.getId())).thenReturn(Optional.of(expectedBeer));
        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        // then
        BeerDTO incrementedBeerDTO = beerService.increment(expectedBeer.getId(), incrementQuantity);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedBeerDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThanOrEqualTo(expectedBeer.getMax()));
    }

    @Test
    void whenIncrementIsCalledExceedsMaxThenThrowException() {
        // given
        Beer expectedBeer = createFakeModel();
        int incrementQuantity = 60; // Max é 50, quantidade atual é 10. 10 + 60 = 70.

        // when
        when(beerRepository.findById(expectedBeer.getId())).thenReturn(Optional.of(expectedBeer));

        // then
        assertThrows(BeerStockExceededException.class, () -> beerService.increment(expectedBeer.getId(), incrementQuantity));
    }
    
    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int incrementQuantity = 10;
        
        // when
        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());
        
        // then
        assertThrows(BeerNotFoundException.class, () -> beerService.increment(INVALID_BEER_ID, incrementQuantity));
    }
    
    // --- TESTES DE TDD (DECREMENTO) ---
    
    @Test
    void whenDecrementIsCalledThenDecrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
        // given
        BeerDTO expectedBeerDTO = createFakeDTO();
        Beer expectedBeer = createFakeModel();
        int decrementQuantity = 5;
        int expectedQuantityAfterDecrement = expectedBeer.getQuantity() - decrementQuantity;

        // when
        when(beerRepository.findById(expectedBeer.getId())).thenReturn(Optional.of(expectedBeer));
        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        // then
        BeerDTO decrementedBeerDTO = beerService.decrement(expectedBeer.getId(), decrementQuantity);

        assertThat(expectedQuantityAfterDecrement, equalTo(decrementedBeerDTO.getQuantity()));
        assertThat(expectedQuantityAfterDecrement, greaterThanOrEqualTo(0));
    }

    @Test
    void whenDecrementIsCalledToSubtractMoreThanStockThenThrowException() {
        // given
        Beer expectedBeer = createFakeModel(); // Quantity = 10
        int decrementQuantity = 15; 

        // when
        when(beerRepository.findById(expectedBeer.getId())).thenReturn(Optional.of(expectedBeer));

        // then
        assertThrows(BeerStockExceededException.class, () -> beerService.decrement(expectedBeer.getId(), decrementQuantity));
    }
    
    @Test
    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
        int decrementQuantity = 5;
        
        // when
        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());
        
        // then
        assertThrows(BeerNotFoundException.class, () -> beerService.decrement(INVALID_BEER_ID, decrementQuantity));
    }
}
