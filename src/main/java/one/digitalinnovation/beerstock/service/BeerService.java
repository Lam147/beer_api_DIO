package one.digitalinnovation.beerstock.service;

import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.entity.Beer;
import one.digitalinnovation.beerstock.exception.*;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.repository.BeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeerService {

    private final BeerRepository repo;
    private final BeerMapper mapper = BeerMapper.INSTANCE;

    @Autowired
    public BeerService(BeerRepository repo) {
        this.repo = repo;
    }

    public BeerDTO createBeer(BeerDTO dto) throws BeerAlreadyRegisteredException {
        if (repo.findByName(dto.getName()).isPresent()) {
            throw new BeerAlreadyRegisteredException(dto.getName());
        }
        return mapper.toDTO(repo.save(mapper.toModel(dto)));
    }

    // LINHA 33/35: Deve usar a exceção que aceita String
    public BeerDTO findByName(String name) throws BeerNotFoundException {
        return mapper.toDTO(repo.findByName(name).orElseThrow(() -> new BeerNotFoundException(name)));
    }

    public List<BeerDTO> listAll() {
        return repo.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    public void deleteById(Long id) throws BeerNotFoundException {
        // Usa a exceção que aceita Long
        repo.findById(id).orElseThrow(() -> new BeerNotFoundException(id));
        repo.deleteById(id);
    }

    public BeerDTO increment(Long id, int qty) throws BeerNotFoundException, BeerStockExceededException {
        Beer b = repo.findById(id).orElseThrow(() -> new BeerNotFoundException(id));
        int n = b.getQuantity() + qty;
        if (n > b.getMax()) throw new BeerStockExceededException(id, n); 
        b.setQuantity(n);
        return mapper.toDTO(repo.save(b));
    }

    public BeerDTO decrement(Long id, int qty) throws BeerNotFoundException, BeerStockExceededException {
        Beer b = repo.findById(id).orElseThrow(() -> new BeerNotFoundException(id));
        int n = b.getQuantity() - qty;
        if (n < 0) throw new BeerStockExceededException(id, n); 
        b.setQuantity(n);
        return mapper.toDTO(repo.save(b));
    }
}
