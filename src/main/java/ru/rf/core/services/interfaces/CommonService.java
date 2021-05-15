package ru.rf.core.services.interfaces;

import org.springframework.stereotype.Service;
import ru.rf.core.entities.interfaces.IdentifiableImpl;

import java.util.List;
import java.util.Optional;

@Service
public interface CommonService<E extends IdentifiableImpl> {
    Optional<E> save(E entity);

    List<E> getAll();
}