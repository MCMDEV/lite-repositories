package de.mcmdev.literepositories;

/**
 * An object that is identifiable by an unique id.
 *
 * @param <ID> The ID type.
 */
public interface Identifiable<ID>
{

    /**
     * Gets the id.
     *
     * @return The id.
     */
    ID getId();

    /**
     * Sets the id.
     *
     * @param id The id.
     */
    void setId(ID id);

}
