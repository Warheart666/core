package org.ls.core.config.listeners;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ls.core.entities.annotations.DeletedDate;
import org.ls.core.exceptions.CustomIllegalAccessException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.lang.reflect.Field;
import java.util.Objects;

@Component
@Slf4j
@NoArgsConstructor
public class CustomAuditingEntityListener extends AuditingEntityListener {


    @Override
    @Autowired
    public void setAuditingHandler(@Nullable ObjectFactory<AuditingHandler> auditingHandler) {
        super.setAuditingHandler(Objects.requireNonNull(auditingHandler));
    }


    @Override
    @PrePersist
    public void touchForCreate(@NonNull Object target) {
        super.touchForCreate(target);
    }


    /**
     * Помимо родительского функционала, ищем есть ли в объекте и его родителях поле enabled,
     * если есть, смотрим на его значение, если false ( объект удалили ) и есть поле с аннотацией @DeletedDate и типом LocalDateTime,
     * присваиваем полю с такой аннотацией значение LocalDateTime.now().
     *
     * @param target объект, который обновляем
     */

    @Override
    @PreUpdate
    public void touchForUpdate(@NonNull Object target) {
        super.touchForUpdate(target);

        Field enabled = ReflectionUtils.findField(target.getClass(), "enabled", Boolean.TYPE);

        if (enabled != null) {
            try {
                ReflectionUtils.makeAccessible(enabled);
                boolean val = (boolean) enabled.get(target);
                if (!val) {
                    ReflectionUtils.doWithFields(target.getClass(), field -> {
                        ReflectionUtils.makeAccessible(field);
                        field.set(target, System.currentTimeMillis());
                    }, field -> field.isAnnotationPresent(DeletedDate.class) && field.getType().equals(Long.TYPE));
                }
            } catch (IllegalAccessException e) {
                log.error("Невозможно установить дату удаления. Ошибка доступа. \n{} \n{}", e.getLocalizedMessage(), e.getStackTrace());
                throw new CustomIllegalAccessException("Невозможно установить дату удаления. Ошибка доступа.");
            }

        }
    }
}
