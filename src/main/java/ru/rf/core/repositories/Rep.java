package ru.rf.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.rf.core.entities.interfaces.IdentifiableImpl;

@NoRepositoryBean
public interface Rep<T extends IdentifiableImpl> extends JpaRepository<T, Long> {
}
