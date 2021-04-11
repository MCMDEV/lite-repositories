package de.mcmdev.literepositories.repository.mongo;

import com.google.gson.Gson;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import de.mcmdev.literepositories.Identifiable;
import de.mcmdev.literepositories.repository.AsyncCrudRepository;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

/**
 * CRUD repository that interacts with a a Mongo collection. Each object will have its own db object associated with its unique id.
 *
 * @param <ID> The id type.
 * @param <T>  The type.
 */
public class MongoRepository<ID, T extends Identifiable<ID>> implements AsyncCrudRepository<ID, T> {

    protected final Gson gson;
    private final MongoCollection<Document> mongoCollection;
    private final Class<T> type;
    private final String idField;

    public MongoRepository(MongoCollection<Document> mongoCollection, Class<T> type, String idField) {
        this.mongoCollection = mongoCollection;
        this.type = type;
        this.idField = idField;
        this.gson = new Gson();
    }

    @Override
    public T save(T object) {
        mongoCollection.replaceOne(eq(idField, object.getId().toString()), Document.parse(gson.toJson(object)), new UpdateOptions().upsert(true), (result, t) -> {
        });
        return object;
    }

    @Override
    public Collection<T> saveAll(Collection<T> objects) {
        ArrayList<T> objectList = new ArrayList<>(objects);
        mongoCollection.insertMany(objectList.stream().map(t -> Document.parse(gson.toJson(t))).collect(Collectors.toList()), (unused, throwable) -> {
        });
        return objectList;
    }

    @Override
    public CompletableFuture<Optional<T>> find(ID id) {
        CompletableFuture<Optional<T>> completableFuture = new CompletableFuture<>();
        mongoCollection.find(eq(idField, id)).limit(1).first((document, throwable) -> {
            if (document == null) {
                completableFuture.complete(Optional.empty());
            }
            completableFuture.complete(Optional.ofNullable(gson.fromJson(document.toJson(), type)));
        });
        return completableFuture;
    }

    @Override
    public CompletableFuture<Collection<T>> findAll() {
        CompletableFuture<Collection<T>> completableFuture = new CompletableFuture<>();
        Set<T> entries = new HashSet<>();
        mongoCollection.find().forEach(document -> gson.fromJson(document.toJson(), type), (unused, throwable) -> {
        });
        completableFuture.complete(entries);
        return completableFuture;
    }

    @Override
    public CompletableFuture<Boolean> delete(T object) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        mongoCollection.deleteOne(eq(idField, object.getId()), (deleteResult, throwable) -> {
            completableFuture.complete(deleteResult.wasAcknowledged());
        });
        return completableFuture;
    }

    @Override
    public CompletableFuture<Boolean> delete(ID id) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        mongoCollection.deleteOne(eq(idField, id), (deleteResult, throwable) -> {
            completableFuture.complete(deleteResult.wasAcknowledged());
        });
        return completableFuture;
    }
}
