package org.ls.core.controllers;

import org.ls.core.controllers.interfaces.CoreController;
import org.ls.core.exceptions.exceptions.EntityNotFoundException;
import org.ls.core.services.interfaces.CoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public abstract class AbstractController<E extends AbstractPersistable<Long>, S extends CoreService<E>> implements CoreController<E> {

    protected final S service;

    @Autowired
    protected AbstractController(S service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<E> get(long id) throws EntityNotFoundException {
        return ResponseEntity.ok(service.get(id));
    }

    @Override
    public ResponseEntity<List<E>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Override
    public ResponseEntity<E> create() {
        return null;
    }

    @Override
    public ResponseEntity<E> update() {
        return null;
    }

    @Override
    public ResponseEntity<E> delete(long id) {
        Optional<E> deleted = service.delete(id);
        return deleted.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }
}
