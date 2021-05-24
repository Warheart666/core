package org.ls.core.repositories;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CoreRepository<T extends AbstractPersistable<Long>> extends JpaRepository<T, Long> {
}
