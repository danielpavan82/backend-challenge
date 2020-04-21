package br.com.ztech.backendchallenge.repository;

import br.com.ztech.backendchallenge.model.CensorshipLevel;
import br.com.ztech.backendchallenge.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {

    Page<Movie> findAllByCensorshipLevelOrderByNameAsc(CensorshipLevel censorshipLevel, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

}
