package pol;

import pol.log.Skip;

/**
 * General description_________________________________________________________
 * A class to represent a simple abstract disease class affecting a person.
 *
 * @author Hossein Amiri (hossein.amiri at emory.edu)
 */

public abstract class DiseaseAbstract implements Condition, java.io.Serializable {
    private static final long serialVersionUID = 1247800092072951970L;
    @Skip
    protected WorldModel model;
    @Skip
    protected WorldParameters params;
    @Skip
    protected Person agent;
    @Skip
    protected DiseaseStatus status;
    @Skip
    protected DiseaseType type;
    @Skip
    protected double pathogenLevel;

    public DiseaseAbstract(Person agent) {
        this.agent = agent;
        this.model = agent.getModel();
        this.params = model.params;
        this.status = DiseaseStatus.None;
        this.type = DiseaseType.None;
        this.pathogenLevel = 0.0;
    }

    @Override
    public void update() {
    }

    public void updatePathogenLevel(double dose) {
        this.pathogenLevel += dose;
    }

    public DiseaseStatus getStatus() {
        return this.status;
    }

    public DiseaseType getType() {
        return this.type;
    }

    public double getPathogenLevel() {
        return this.pathogenLevel;
    }

    public void setPathogenLevel(double pathogenLevel) {
        this.pathogenLevel = pathogenLevel;
    }

}
