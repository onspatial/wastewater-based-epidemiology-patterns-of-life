package pol;

import org.joda.time.Days;
import org.joda.time.LocalDateTime;

import pol.log.Characteristics;
import pol.log.Skip;

/**
 * General description_________________________________________________________
 * A class to represent an abstract class of infectious disease affecting a
 * person.
 *
 * @author Hossein Amiri (hossein.amiri at emory.edu)
 */

public abstract class InfectiousDiseaseAbstract extends DiseaseAbstract {
    private static final long serialVersionUID = 1247800092072951969L;

    @Skip
    protected LocalDateTime susceptibleStartedTime;
    @Skip
    protected LocalDateTime exposedStartedTime;
    @Skip
    protected LocalDateTime infectiousStartedTime;
    @Skip
    protected LocalDateTime recoveredStartedTime;

    @Skip
    protected int numberOfDaysHasBeenExposed;
    @Skip
    protected int numberOfDaysHasBeenInfectious;
    @Skip
    protected int numberOfDaysHasBeenRecovered;

    @Skip
    protected int numberOfDaysToBeExposed;
    @Skip
    protected int numberOfDaysToBeInfectious;
    @Skip
    protected int numberOfDaysToBeRecovered;
    @Skip
    protected double smoothnessRate;

    @Characteristics
    protected double chanceOfInfection;

    @Characteristics
    protected double chanceOfSpreading;

    @Skip
    protected long sourceAgentId;

    public InfectiousDiseaseAbstract(Person agent) {
        super(agent);
        this.status = DiseaseStatus.None;
        this.susceptibleStartedTime = null;
        this.exposedStartedTime = null;
        this.infectiousStartedTime = null;
        this.recoveredStartedTime = null;
        this.numberOfDaysHasBeenExposed = 0;
        this.numberOfDaysHasBeenInfectious = 0;
        this.numberOfDaysHasBeenRecovered = 0;
        this.chanceOfInfection = 0.0;
        this.chanceOfSpreading = 0.0;
        this.sourceAgentId = -1;
        this.numberOfDaysToBeExposed = params.numberOfDaysToBeExposed;
        this.numberOfDaysToBeInfectious = params.numberOfDaysToBeInfectious;
        this.numberOfDaysToBeRecovered = params.numberOfDaysToBeRecovered;
    }

    @Override
    public void update() {
        switch (this.status) {
            case Susceptible:
                this.status = DiseaseStatus.Exposed;
                break;
            case Exposed:
                this.status = DiseaseStatus.Infectious;
                break;
            case Infectious:
                this.status = DiseaseStatus.Recovered;
                break;
            case Recovered:
                this.status = DiseaseStatus.Susceptible;
                break;
            case None:
                // No action needed for None status
                break;
        }
    }

    protected double getRandomValue() {
        return agent.getModel().random.nextDouble();
    }

    protected void smooth() {
        this.numberOfDaysToBeExposed = smoothed(params.numberOfDaysToBeExposed);
        this.numberOfDaysToBeInfectious = smoothed(params.numberOfDaysToBeInfectious);
        this.numberOfDaysToBeRecovered = smoothed(params.numberOfDaysToBeRecovered);
    }

    protected int smoothed(int value) {
        return (int) Math.ceil(value + (0.5 - this.smoothnessRate) * value);
    }

    protected double smoothed(double value) {
        return value + (0.5 - this.smoothnessRate) * value;
    }

    public int getNumberOfDays(LocalDateTime startTime) {
        if (startTime == null) {
            return 0;
        }
        LocalDateTime now = agent.getSimulationTime();
        int differences = Days.daysBetween(startTime, now).getDays();
        return differences;
    }

    public void updateDays() {
        numberOfDaysHasBeenExposed = getNumberOfDays(exposedStartedTime);
        numberOfDaysHasBeenInfectious = getNumberOfDays(infectiousStartedTime);
        numberOfDaysHasBeenRecovered = getNumberOfDays(recoveredStartedTime);
    }

    protected boolean isSpreadingPossible() {
        return this.status == DiseaseStatus.Infectious;
    }

    protected boolean isInfectionPossible() {
        return true;
    }

    public boolean becomeInfected() {
        if (!isInfectionPossible()) {
            return false;
        }
        this.status = DiseaseStatus.Exposed;
        this.exposedStartedTime = this.agent.getSimulationTime();
        return true;
    }

    public boolean infect(Person recipient) {
        if (!isSpreadingPossible()) {
            return false;
        }
        if (recipient != null && recipient != this.agent) {
            return recipient.getInfectiousDisease().becomeInfected(this.agent.getAgentId());
        }
        return false;

    }

    public boolean becomeInfected(long fromAgentId) {
        if (this.becomeInfected()) {
            this.setSourceAgentId(fromAgentId);
            return true;
        }
        return false;
    }

    public LocalDateTime getSusceptibleStartedTime() {
        return this.susceptibleStartedTime;
    }

    public LocalDateTime getExposedStartedTime() {
        return this.exposedStartedTime;
    }

    public LocalDateTime getInfectiousStartedTime() {
        return this.infectiousStartedTime;
    }

    public LocalDateTime getRecoveredStartedTime() {
        return this.recoveredStartedTime;
    }

    public int getNumberOfDaysHasBeenExposed() {
        return this.numberOfDaysHasBeenExposed;
    }

    public int getNumberOfDaysHasBeenInfectious() {
        return this.numberOfDaysHasBeenInfectious;
    }

    public int getNumberOfDaysHasBeenRecovered() {
        return this.numberOfDaysHasBeenRecovered;
    }

    public double getChanceOfInfectious() {
        return this.chanceOfInfection;
    }

    public double getChanceOfSpreading() {
        return this.chanceOfSpreading;
    }

    public void setChanceOfInfection(double chance) {
        this.chanceOfInfection = chance;
    }

    public void setChanceOfSpreading(double chance) {
        this.chanceOfSpreading = chance;
    }

    protected boolean isExposurePossible() {
        return this.status == DiseaseStatus.Susceptible;
    }

    public void setSourceAgentId(long sourceAgentId) {
        this.sourceAgentId = sourceAgentId;
    }

    public long getSourceAgentId() {
        return sourceAgentId;
    }

    public void setDiseaseStatus(DiseaseStatus status) {
        this.status = status;
    }

    public boolean isInfectious() {
        return this.status == DiseaseStatus.Infectious;
    }

    public int getNumberOfDaysToBeExposed() {
        return numberOfDaysToBeExposed;
    }

    public int getNumberOfDaysToBeInfectious() {
        return numberOfDaysToBeInfectious;
    }

    public int getNumberOfDaysToBeRecovered() {
        return numberOfDaysToBeRecovered;
    }

    public double getSmoothnessRate() {
        return this.smoothnessRate;
    }

    public void setSmoothnessRate(double smoothnessRate) {
        this.smoothnessRate = smoothnessRate;
        smooth();
    }

}
