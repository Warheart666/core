package org.ls.core.entities.interfaces;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.ls.core.entities.models.auth.User;
import org.springframework.data.jpa.domain.AbstractAuditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@ToString(callSuper = true)
@Getter
public abstract class BasicEntity<T extends BasicEntity<T>> extends AbstractAuditable<User, Long> implements Versionable {

    protected Class<T> clazz;


    public abstract void setClazz(Class<T> aClass);

//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "auto_gen")
//    @SequenceGenerator(name = "auto_gen", sequenceName = "main_seq")
//    @Column(name = "ID")
//    private Long id;
}