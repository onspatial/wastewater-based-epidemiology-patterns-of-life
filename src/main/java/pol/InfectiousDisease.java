package pol;

import pol.log.Characteristics;
import pol.log.Skip;

/**
 * General description_________________________________________________________
 * A class to represent infectious disease affecting a person.
 * This class extends the Disease class and uses statistics to control the
 * disease progression.
 *
 * @author Hossein Amiri (hossein.amiri at emory.edu)
 */

public class InfectiousDisease extends InfectiousDiseaseAbstract {
    private static final long serialVersionUID = 1247800092072951968L;

    @Skip
    private int numberOfDaysToBeExposed;
    @Skip
    private int numberOfDaysToBeInfectious;
    @Skip
    private int numberOfDaysToBeRecovered;
    @Skip
    private double smoothnessRate;

    @Skip
    private int spreadCounter;

    @Characteristics
    private double sheddingRate;

    public InfectiousDisease(Person agent) {
        super(agent);
        this.status = DiseaseStatus.Susceptible;
        this.susceptibleStartedTime = agent.getSimulationTime();
        this.numberOfDaysToBeExposed = params.numberOfDaysToBeExposed;
        this.numberOfDaysToBeInfectious = params.numberOfDaysToBeInfectious;
        this.numberOfDaysToBeRecovered = params.numberOfDaysToBeRecovered;
        this.spreadCounter = 0;
        this.sheddingRate = 0.0;

    }

    public void resetSpreadCounter() {
        this.spreadCounter = 0;
    }

    private void smooth() {
        this.numberOfDaysToBeExposed = smoothed(params.numberOfDaysToBeExposed);
        this.numberOfDaysToBeInfectious = smoothed(params.numberOfDaysToBeInfectious);
        this.numberOfDaysToBeRecovered = smoothed(params.numberOfDaysToBeRecovered);
    }

    private int smoothed(int value) {
        return (int) Math.ceil(value + (0.5 - this.smoothnessRate) * value);
    }

    private double smoothed(double value) {
        return value + (0.5 - this.smoothnessRate) * value;
    }

    @Override
    public void update() {
        updateDays();
        updatePathogenLevel();
        switch (this.status) {
            case Susceptible:
                // live a normal life
                break;
            case Exposed:
                if (canBeInfectious()) {
                    this.status = DiseaseStatus.Infectious;
                    this.infectiousStartedTime = agent.getSimulationTime();
                }
                break;
            case Infectious:
                if (canBeRecovered()) {
                    this.status = DiseaseStatus.Recovered;
                    this.recoveredStartedTime = agent.getSimulationTime();
                }
                break;
            case Recovered:
                if (canBeSusceptible()) {
                    resetSpreadCounter();
                    this.status = DiseaseStatus.Susceptible;
                    this.susceptibleStartedTime = agent.getSimulationTime();
                    this.exposedStartedTime = null;
                    this.infectiousStartedTime = null;
                    this.recoveredStartedTime = null;
                }
                break;
            case None:
                // No action needed for None status
                break;
        }
    }

    public double getRandomValue() {
        return agent.getModel().random.nextDouble();
    }

    @Override
    protected boolean isSpreadingPossible() {
        return isInfectious() && this.spreadCounter < params.numberOfSpreadPerPerson
                && this.chanceOfSpreading > getRandomValue();
    }

    @Override
    protected boolean isInfectionPossible() {
        return this.chanceOfInfection > getRandomValue() && params.infectionRatio > getRandomValue();
    }

    @Override
    public boolean infect(Person recipient) {
        boolean isInfected = false;
        if (!isSpreadingPossible()) {
            return isInfected;
        }
        if (recipient != null && recipient != this.agent) {
            isInfected = recipient.getInfectiousDisease().infect();
        }
        this.spreadCounter++;
        return isInfected;
    }

    @Override
    public boolean infect() {
        if (!isInfectionPossible()) {
            return false;
        }
        this.status = DiseaseStatus.Exposed;
        this.exposedStartedTime = this.agent.getSimulationTime();
        return true;
    }

    private boolean canBeInfectious() {
        return this.status == DiseaseStatus.Exposed
                && this.numberOfDaysToBeExposed <= this.numberOfDaysHasBeenExposed;
    }

    private boolean canBeRecovered() {
        return isInfectious()
                && this.numberOfDaysToBeInfectious <= this.numberOfDaysHasBeenInfectious;
    }

    private boolean canBeSusceptible() {
        return this.status == DiseaseStatus.Recovered
                && this.numberOfDaysToBeRecovered <= this.numberOfDaysHasBeenRecovered;
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

    public double getSheddingRate() {
        return this.sheddingRate;
    }

    public void setSheddingRate(double sheddingRate) {
        this.sheddingRate = sheddingRate;
    }

    public void updatePathogenLevel() {
        double a = 2;
        double shape = 8;
        double t = this.numberOfDaysHasBeenInfectious;
        double scale = 10000000;
        scale = scale * (Math.pow(this.sheddingRate, 2));
        this.pathogenLevel = (scale * (Math.pow(t, shape)) * Math.exp(-a * t));
    }

    public double getSpreadCounter() {
        return this.spreadCounter;
    }

    public void setDiseaseStatus(DiseaseStatus status) {
        this.status = status;
    }

    public boolean isInfectious() {
        return this.status == DiseaseStatus.Infectious;
    }
}
