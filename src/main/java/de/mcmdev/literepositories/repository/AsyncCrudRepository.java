package de.mcmdev.literepositories.repository;

import de.mcmdev.literepositories.Identifiable;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Repository that covers all the basic CRUD operations (Create, Read, Update, and Delete) asynchronously.
 *
 * @param <ID> The id type.
 * @param <T>  The object type.
 */
public interface AsyncCrudRepository<ID, T extends Identifiable<ID>> extends Repository<ID, T> {

    /**
     * Saves the object.
     *
     * @param object The object.
     * @return The saved object.
     */
    T save(T object);

    /**
     * Saves the objects.
     *
     * @param objects The objects.
     * @return The saved objects.
     */
    Collection<T> saveAll(Collection<T> objects);

    /**
     * Finds a object by its id.
     *
     * @param id The id.
     * @return The object.
     */
    CompletableFuture<Optional<T>> find(ID id);

    /**
     * Retrieves all the objects.
     *
     * @return The objects.
     */
    CompletableFuture<Collection<T>> findAll();

    /**
     * Deletes the object.
     *
     * @param object The object.
     * @return True if deleted.
     */
    CompletableFuture<Boolean> delete(T object);

    /**
     * Deletes the object by ID.
     *
     * @param id The ID of the object.
     * @return True if deleted.
     */
    CompletableFuture<Boolean> delete(ID id);

}
