package org.ls.core.services.interfaces;

import org.ls.core.exceptions.exceptions.EntityNotFoundException;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CoreService<E extends AbstractPersistable<Long>> {
    List<E> getAll();

    E get(long id) throws EntityNotFoundException;

    Optional<E> save(E entity);

    Optional<E> delete(long id);

}