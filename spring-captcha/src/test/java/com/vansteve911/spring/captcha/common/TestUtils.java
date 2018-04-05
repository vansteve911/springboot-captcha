package com.vansteve911.spring.captcha.common;

import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by vansteve911 on 18/4/6.
 */
public class TestUtils {

    public static void expectExceptionThrown(ExpectedException thrown,
                                             Class<? extends Exception> expectedType,
                                             String expectedMessage) {
        thrown.expect(expectedType);
        thrown.expectMessage(expectedMessage);
    }

    public static Object getPrivateFieldValue(Object object, String fieldName) {
        Field field = getFieldByFieldName(object, fieldName);
        Object value = null;
        try {
            if (null != field) {
                if (field.isAccessible()) {
                    value = field.get(object);
                } else {
                    field.setAccessible(true);
                    value = field.get(object);
                    field.setAccessible(false);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public static void setPrivateFieldValue(Object object, String fieldName, Object value) {
        try {
            Field field = getFieldByFieldName(object, fieldName);
            boolean originalAccessState = field.isAccessible();
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(originalAccessState);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokePrivateMethod(Object object,
                                             String methodName,
                                             Class<?>[] parameterTypes,
                                             Object[] args) {
        Method method = null;
        boolean originalAccessState = false;
        try {
            method = object.getClass().getDeclaredMethod(methodName, parameterTypes);
            originalAccessState = method.isAccessible();
            method.setAccessible(true);
            return method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (method != null) {
                try {
                    method.setAccessible(originalAccessState);
                } catch (Exception ignore) {
                }
            }
        }
    }

    public static Object invokePrivateMethod(Object object,
                                             String methodName) {
        return invokePrivateMethod(object, methodName, new Class[0], new Object[0]);
    }

    private static Field getFieldByFieldName(Object object, String fieldName) {
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            Field[] fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                if (fieldName.equalsIgnoreCase(field.getName())) {
                    return field;
                }
            }
        }
        return null;
    }
}
