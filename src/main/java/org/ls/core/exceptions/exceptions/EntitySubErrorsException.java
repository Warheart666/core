package org.ls.core.exceptions.exceptions;

import lombok.Getter;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class EntitySubErrorsException extends Exception {

    private List<FieldError> errors = new ArrayList<>();

    public EntitySubErrorsException(@NotNull Class<?> clazz, String... searchParamsMap) {
        super(EntitySubErrorsException.generateMessage(clazz.getSimpleName(), toMap(searchParamsMap)));

        Map<String, String> errorsMap = toMap(searchParamsMap);

        errorsMap.entrySet().forEach(stringStringEntry -> this.errors.add(new FieldError(clazz.getSimpleName(), stringStringEntry.getKey(), stringStringEntry.setValue(stringStringEntry.getKey()))));
    }

    @NotNull
    private static String generateMessage(String entity, Map<String, String> searchParams) {
        return StringUtils.capitalize(entity) + " несколько полей не прошло валидаию " + searchParams;
    }

    private static Map<String, String> toMap(@NotNull String... entries) {

        if (entries.length % 2 == 1)
            throw new IllegalArgumentException("Invalid entries");

        HashMap<String, String> m = new HashMap<>();

        int bound = entries.length / 2;

        for (int i = 0; i < bound; i++) {
            int i1 = i * 2;
            m.put(entries[i1], entries[i1 + 1]);
        }
        return m;
    }

}