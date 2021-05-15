package ru.rf.core.web;

import org.springframework.beans.factory.annotation.Autowired;
import ru.rf.core.entities.interfaces.IdentifiableImpl;
import ru.rf.core.services.interfaces.CommonService;
import ru.rf.core.web.interfaces.CommonController;

import java.util.List;

public abstract class AbstrContr<E extends IdentifiableImpl, S extends CommonService<E>> implements CommonController<E> {

    protected final S service;

    @Autowired
    protected AbstrContr(S service) {
        this.service = service;
    }

    @Override
    public E get(long id) {
        return null;
    }

    @Override
    public List<E> getAll() {
        return service.getAll();
    }
}
