package de.mcmdev.literepositories.repository;

import de.mcmdev.literepositories.Identifiable;

/**
 * Repository that stores a type associated with its id.
 *
 * @param <ID> The id type.
 * @param <T> The object type.
 */
public interface Repository<ID, T extends Identifiable<ID>>
{

}
