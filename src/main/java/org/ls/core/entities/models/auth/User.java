package org.ls.core.entities.models.auth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ls.core.entities.interfaces.BasicEntity;

import javax.persistence.Entity;

@Entity
@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends BasicEntity<User> {

    {
        setClazz(User.class);
    }

    @Override
    public void setClazz(Class<User> aClass) {
        this.clazz = aClass;
    }
}
