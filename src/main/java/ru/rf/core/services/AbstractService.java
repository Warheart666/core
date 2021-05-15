package ru.rf.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import ru.rf.core.entities.interfaces.IdentifiableImpl;
import ru.rf.core.repositories.Rep;
import ru.rf.core.services.interfaces.CommonService;

import java.util.List;
import java.util.Optional;

public abstract class AbstractService<E extends IdentifiableImpl, R extends Rep<E>> implements CommonService<E> {

    protected final R repository;

    @Autowired
    protected AbstractService(R repository) {
        this.repository = repository;
    }


    @Override
    public List<E> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<E> save(E entity) {
        return Optional.empty();
    }
}
