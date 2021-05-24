package org.ls.core.utils;

import org.ls.core.exceptions.GenericClassDetermineException;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

public class CoreUtils {

    public static Class<?> getGenericCLassFromFirstType(Object o) {
        Objects.requireNonNull(o, "Object can`t be null");
        ParameterizedType type = (ParameterizedType) o.getClass().getGenericSuperclass();
        var arguments = type.getActualTypeArguments();
        var clazz = arguments.length > 0 ? arguments[0] : null;
        Objects.requireNonNull(clazz, "Must be generic!");
        if (clazz instanceof Class)
            return ((Class<?>) clazz);

        throw new GenericClassDetermineException("Can`t determine generic class");

    }

}
