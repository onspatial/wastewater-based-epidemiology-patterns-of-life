package pol;

/**
 * General description_________________________________________________________
 * An enumeration to keep track of disease status of the agent. If not healthy,
 * this
 * enumeration shows how the agent is affected by diseases.
 * 
 * @author Hossein Amiri (hossein.amiri at emory.edu)
 * 
 */
public enum DiseaseStatus {
    None, // no disease
    Susceptible, // not infected, but can be infected
    Exposed, // exposed to the disease but not yet infectious
    Infectious, // infected and can infect others
    Recovered, // recovered from the disease

}
