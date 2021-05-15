package ru.rf.core.entities.interfaces;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class IdentifiableImpl extends BasicEntity {

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }
}
