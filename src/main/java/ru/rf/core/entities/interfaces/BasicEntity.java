package ru.rf.core.entities.interfaces;


import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.AbstractAuditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.rf.core.auth.User;

import javax.persistence.*;

@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
public abstract class BasicEntity extends AbstractAuditable<User, Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "auto_gen")
    @SequenceGenerator(name = "auto_gen", sequenceName = "A")
    @Column(name = "ID")
    private Long id;

    @Version
    private long version;

}