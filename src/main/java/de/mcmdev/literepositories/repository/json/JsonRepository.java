package de.mcmdev.literepositories.repository.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.mcmdev.literepositories.Identifiable;
import de.mcmdev.literepositories.repository.CrudRepository;
import lombok.AllArgsConstructor;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CRUD repository that interacts with a JSON files. Each object will have its own file associated with its unique id.
 *
 * @param <ID> The id type.
 * @param <T> The type.
 */
@AllArgsConstructor
public class JsonRepository<ID, T extends Identifiable<ID>> implements CrudRepository<ID, T>
{

    /**
     * The data folder.
     */
    private File dataFolder;

    /**
     * The class type.
     */
    private Class<T> type;

    /**
     * The storage file path.
     */
    private String path;

    /**
     * The GSON instance.
     */
    protected Gson gson;

    /**
     * Constructs a new {@link JsonRepository}.
     *
     * @param dataFolder The data folder.
     * @param type       The type.
     * @param path       The path.
     */
    public JsonRepository(File dataFolder, Class<T> type, String path) {
        this(dataFolder, type, path, new GsonBuilder().setPrettyPrinting().create());
    }

    /**
     * Gets the storage file for the provided id. Will create dirs/files if necessary.
     *
     * @param id The id.
     * @return The file.
     */
    protected File getFile(ID id)
    {
        final File file = new File(
                dataFolder + File.separator + this.path, id.toString() + ".json"
        );

        file.getParentFile().mkdirs();
        return file;
    }

    /**
     * Gets the storage file for the provided object.
     *
     * @param object The object.
     * @return The file.
     */
    protected File getFile(T object)
    {
        return this.getFile(object.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T save(T object)
    {
        // Write.
        try (Writer writer = new FileWriter(this.getFile(object))) {
            gson.toJson(object, writer);
        } catch (IOException e) {
            return null;
        }
        return object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> saveAll(Collection<T> objects)
    {
        List<T> saved = new ArrayList<>();
        objects.forEach(object -> saved.add(this.save(object)));
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> find(ID id) {
        // Read.
        try {
            return Optional.ofNullable(gson.fromJson(new FileReader(this.getFile(id)), type));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> findAll() {
        File pathFolder = new File(dataFolder + File.separator + this.path);
        pathFolder.mkdirs();
        return Arrays.stream(pathFolder.listFiles((dir, name) -> name.endsWith(".json"))).map(file -> {
            try (FileReader fileReader = new FileReader(file)) {
                return gson.fromJson(fileReader, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(T object)
    {
        return this.getFile(object).delete();
    }

}
