package pol;

/**
 * General description_________________________________________________________
 * Minimum set of methods that each condition has to implement.
 * 
 * @author Hossein Amiri (hossein.amiri at emory.edu)
 * 
 */

public interface Condition {

    /**
     * Should include logic to update the state of the condition over time.
     */
    void update();

}