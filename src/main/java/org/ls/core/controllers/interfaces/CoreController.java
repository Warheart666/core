package org.ls.core.controllers.interfaces;

import org.ls.core.exceptions.exceptions.EntityNotFoundException;
import org.ls.core.exceptions.exceptions.EntitySubErrorsException;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public interface CoreController<E extends AbstractPersistable<Long>> {

    @GetMapping("/{id:[\\d]+}")
    ResponseEntity<E> get(@PathVariable long id) throws EntityNotFoundException, EntitySubErrorsException;

    @GetMapping
    ResponseEntity<List<E>> getAll();


    @PostMapping
    ResponseEntity<E> create();


    @PutMapping
    ResponseEntity<E> update();


    @DeleteMapping("/{id:[\\d]+}")
    ResponseEntity<E> delete(@PathVariable long id);


}
