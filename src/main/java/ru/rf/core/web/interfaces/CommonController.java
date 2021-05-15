package ru.rf.core.web.interfaces;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.rf.core.entities.interfaces.IdentifiableImpl;

import java.util.List;

@RestController
public interface CommonController<E extends IdentifiableImpl> {

    @GetMapping("/{id:[\\d]+}")
    E get(@PathVariable long id);

    @GetMapping
    List<E> getAll();


}
