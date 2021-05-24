package org.ls.core.services;

import org.ls.core.entities.interfaces.BasicEntity;
import org.ls.core.exceptions.exceptions.EntityNotFoundException;
import org.ls.core.repositories.CoreRepository;
import org.ls.core.services.interfaces.CoreService;
import org.ls.core.utils.CoreUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public abstract class AbstractService<E extends BasicEntity<E>, R extends CoreRepository<E>> implements CoreService<E> {

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
    public E get(long id) throws EntityNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> getNotFoundException("id", String.valueOf(id)));
    }

    @Override
    public Optional<E> save(E entity) {
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(long id) {
        return Optional.empty();
    }

    private EntityNotFoundException getNotFoundException(String fieldName, String fieldValue) {
        return new EntityNotFoundException(CoreUtils.getGenericCLassFromFirstType(this), fieldName, fieldValue);
    }


}
