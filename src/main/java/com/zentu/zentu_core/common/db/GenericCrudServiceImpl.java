package com.zentu.zentu_core.common.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

@Service
public class GenericCrudServiceImpl implements GenericCrudService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public <T> T create(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public <T> T read(Class<T> entityClass, UUID id) {
        return entityManager.find(entityClass, id);
    }

    @Override
    @Transactional
    public <T> T updateFields(Class<T> entityClass, UUID id, Map<String, Object> fieldsToUpdate) {
        T entity = entityManager.find(entityClass, id);
        if (entity == null) {
            throw new RuntimeException("Entity not found: " + id);
        }

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            try {
                Field field = entityClass.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                field.set(entity, entry.getValue());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Error updating field: " + entry.getKey(), e);
            }
        }

        return entityManager.merge(entity);
    }

    @Override
    @Transactional
    public <T> void delete(Class<T> entityClass, UUID id) {
        T entity = entityManager.find(entityClass, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    @Transactional
    public <T> T save(T entity) {
        return entityManager.merge(entity);
    }


    @Override
    public <T> T findOneByField(Class<T> entityClass, String fieldName, Object value) {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e." + fieldName + " = :value";
        return entityManager.createQuery(jpql, entityClass)
                .setParameter("value", value)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

}
