package pol;

import org.joda.time.LocalDateTime;

import pol.log.Skip;
import pol.log.Skip;

/**
 * General description_________________________________________________________
 * A class to represent a disease affecting a person.
 *
 * @author Hossein Amiri (hossein.amiri at emory.edu)
 */

public class InfectiousDiseaseGermTheory implements Condition, java.io.Serializable {
    private static final long serialVersionUID = 1247800092072951971L;
    @Skip
    protected WorldModel model;
    @Skip
    protected WorldParameters params;
    @Skip
    protected Person agent;
    @Skip
    private double pathogenLevel;
    @Skip
    private double immunityLevel;
    @Skip
    private double infectionThreshold;
    @Skip
    private double exposureThreshold;
    @Skip
    private DiseaseStatus status;
    @Skip
    private LocalDateTime infectionTime;

    public InfectiousDiseaseGermTheory(Person agent) {
        this.status = DiseaseStatus.Susceptible;
        this.agent = agent;
        this.model = agent.getModel();
        this.params = model.params;
        this.infectionThreshold = params.defaultInfectionThreshold;
        this.exposureThreshold = params.defaultExposureThreshold;
        this.immunityLevel = 0.0;
        this.pathogenLevel = 0.0;
    }

    @Override
    public void update() {
        switch (this.status) {
            case Susceptible:
                stressPathogen();
                if (this.pathogenLevel >= this.exposureThreshold && this.pathogenLevel < infectionThreshold) {
                    this.status = DiseaseStatus.Exposed;
                }
                break;
            case Exposed:
                stressPathogen();
                if (this.pathogenLevel >= infectionThreshold) {
                    setImmunityLevel(this.immunityLevel - params.defaultImmunityDecayRate);
                    this.status = DiseaseStatus.Infectious;
                    setPathogenLevel(1);
                }
                break;
            case Infectious:
                fightPathogen();
                if (this.pathogenLevel < infectionThreshold) {
                    setImmunityLevel(this.immunityLevel + params.defaultImmunityDecayRate);
                    this.status = DiseaseStatus.Recovered;
                    setPathogenLevel(0.0);
                }
                break;
            case Recovered:
                if (isSusceptible()) {
                    this.status = DiseaseStatus.Susceptible;
                    this.infectionTime = null;
                    setPathogenLevel(0.0);
                }
                break;
        }
    }

    private boolean isSusceptible() {
        return this.immunityLevel <= 0.0 && this.pathogenLevel <= 0.0;
    }

    private boolean isExposable() {
        return this.pathogenLevel >= this.exposureThreshold;
    }

    private boolean isInfectable() {
        return this.pathogenLevel >= this.infectionThreshold;
    }

    private boolean isRecoverable() {
        return this.immunityLevel <= 1;
    }

    public void stressPathogen() {
        replicatePathogen();
        fightPathogen();
    }

    private void fightPathogen() {
        setPathogenLevel(this.pathogenLevel - (this.pathogenLevel * this.immunityLevel) *
                params.defaultPathogenDeathRate);
    }

    private void replicatePathogen(double multiplier) {
        setPathogenLevel(this.pathogenLevel
                + (this.pathogenLevel * multiplier));
    }

    private void replicatePathogen() {
        replicatePathogen(params.defaultPathogenGrowthRate);
    }

    public void infect(Person recipient, double dose) {
        if (this.status != DiseaseStatus.Infectious) {
            return;
        }
        if (recipient != null && recipient != this.agent) {
            recipient.getInfectiousDisease().infect(dose);
        }
    }

    public void infect(Person recipient) {
        if (this.status != DiseaseStatus.Infectious) {
            return;
        }
        if (recipient != null && recipient != this.agent) {
            recipient.getInfectiousDisease().infect(this.pathogenLevel * params.defaultPathogenTransmissionRate);
        }
    }

    public void infect(double dose) {
        setPathogenLevel(this.pathogenLevel + dose);
        this.infectionTime = this.agent.getSimulationTime();
    }

    public DiseaseStatus getStatus() {
        return this.status;
    }

    public LocalDateTime getInfectionTime() {
        return this.infectionTime;
    }

    public double getPathogenLevel() {
        return this.pathogenLevel;
    }

    public double getImmunityLevel() {
        return this.immunityLevel;
    }

    public void setPathogenLevel(double level) {
        this.pathogenLevel = Math.max(0, level);

    }

    public void setImmunityLevel(double level) {
        this.immunityLevel = Math.max(0, level);
    }
}
