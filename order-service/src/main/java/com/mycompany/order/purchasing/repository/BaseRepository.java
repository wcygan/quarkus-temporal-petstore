package com.mycompany.order.purchasing.repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.micrometer.core.annotation.Timed;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

/**
 * Base repository providing common methods for saving or updating entities.
 *
 * @param <T> the type of the entity managed by this repository
 
 */
public class BaseRepository<T> implements PanacheRepository<T> {

    /**
     * Saves or updates the given entity.
     *
     * @param entity the entity to save or update
     * @return the saved or updated entity
     * @throws NullPointerException if the entity is {@code null}
     */
    @Timed(value = "repo.base.saveOrUpdate", description = "Saves or updates an entity")
    public T saveOrUpdate(T entity) {
        Objects.requireNonNull(entity, "Cannot save or update a null entity");
        return getEntityManager().merge(entity);
    }

    /**
     * Saves or updates the given list of entities.
     *
     * @param entities the list of entities to save or update
     * @return a list of saved or updated entities
     */
    @Timed(value = "repo.base.saveOrUpdate.list", description = "Saves or updates list of entity")
    public List<T> saveOrUpdate(List<? extends T> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities
                .stream()
                .map(this::saveOrUpdate)
                .collect(Collectors.toList());
    }

    /**
     * Saves or updates the given set of entities.
     *
     * @param entities the set of entities to save or update
     * @return a set of saved or updated entities
     */
    @Timed(value = "repo.base.saveOrUpdate.set", description = "Saves or updates Set of entity")
    public Set<T> saveOrUpdate(Set<? extends T> entities) {
        if (entities == null) {
            return Collections.emptySet();
        }
        return entities
                .stream()
                .map(this::saveOrUpdate)
                .collect(Collectors.toSet());
    }
}
