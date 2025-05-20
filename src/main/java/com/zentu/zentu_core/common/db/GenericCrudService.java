package com.zentu.zentu_core.common.db;

import java.util.Map;
import java.util.UUID;

public interface GenericCrudService {
    <T> T create(T entity);

    <T> T read(Class<T> entityClass, UUID id);

    <T> T updateFields(Class<T> entityClass, UUID id, Map<String, Object> fieldsToUpdate);

    <T> void delete(Class<T> entityClass, UUID id);

    <T> T findOneByField(Class<T> entityClass, String fieldName, Object value);

}
