package pol;

import java.util.List;

import pol.log.Characteristics;
import pol.log.Skip;
import pol.utils.CollectionUtil;

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
    private int spreadCounter;

    @Characteristics
    private double sheddingRate;

    public InfectiousDisease(Person agent) {
        super(agent);
        this.setDiseaseStatus(DiseaseStatus.Susceptible);
        this.susceptibleStartedTime = agent.getSimulationTime();

        this.spreadCounter = 0;
        this.sheddingRate = 0.0;

    }

    public void resetSpreadCounter() {
        this.spreadCounter = 0;
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
                    this.setDiseaseStatus(DiseaseStatus.Infectious);
                    this.infectiousStartedTime = agent.getSimulationTime();
                }
                break;
            case Infectious:
                if (canBeRecovered()) {
                    this.setDiseaseStatus(DiseaseStatus.Recovered);
                    this.recoveredStartedTime = agent.getSimulationTime();
                }
                break;
            case Recovered:
                if (canBeSusceptible()) {
                    resetSpreadCounter();
                    this.setDiseaseStatus(DiseaseStatus.Susceptible);
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

    @Override
    protected boolean isSpreadingPossible() {
        return isInfectious() && this.spreadCounter < params.numberOfSpreadPerPerson
                && this.chanceOfSpreading > getRandomValue();
    }

    @Override
    protected boolean isInfectionPossible() {
        return this.chanceOfInfection > getRandomValue() && params.infectionRatio > getRandomValue();
    }

    public void spreadInfectionToOneAgent(Person anotherAgent) {
        if (this.agent.getInfectiousDisease() != null && this.agent.getInfectiousDisease().isInfectious()) {
            this.agent.getInfectiousDisease().infect(anotherAgent);
        } else if (anotherAgent.getInfectiousDisease() != null
                && anotherAgent.getInfectiousDisease().isInfectious()) {
            anotherAgent.getInfectiousDisease().infect(this.agent);

        }
    }

    public void spreadInfectionInCurrentUnit(int numberOfAgentsToInfect) {
        if (agent.getCurrentUnit() == null) {
            return;
        }
        List<Person> agentsInTheSameUnit = agent.getCurrentUnit().getCurrentAgents();
        spreadInfectionToManyAgents(agentsInTheSameUnit, numberOfAgentsToInfect);
    }

    public void spreadInfectionToManyAgents(List<Person> listOfAgentsToInfect, int numberOfAgentsToInfect) {
        listOfAgentsToInfect.remove(agent);
        if (listOfAgentsToInfect.size() == 0) {
            return;
        }
        CollectionUtil.shuffle(listOfAgentsToInfect, agent.getModel().random);
        for (int count = 0; count < listOfAgentsToInfect.size() && count < numberOfAgentsToInfect; count++) {
            Person agentToInfect = listOfAgentsToInfect.get(count);
            agent.getInfectiousDisease().spreadInfectionToOneAgent(agentToInfect);
        }
    }

    public void spreadInfectionToOneAgent(long anotherAgentId) {
        Person anotherAgent = agent.getModel().getAgent(anotherAgentId);
        spreadInfectionToOneAgent(anotherAgent);
    }

    @Override
    public boolean infect(Person recipient) {
        boolean isInfected = false;
        if (!isSpreadingPossible()) {
            return isInfected;
        }
        if (recipient != null && recipient != this.agent) {
            isInfected = recipient.getInfectiousDisease().becomeInfected(this.agent.getAgentId());
        }
        this.spreadCounter++;
        return isInfected;
    }

    @Override
    public boolean becomeInfected() {
        if (!isInfectionPossible()) {
            return false;
        }
        this.setDiseaseStatus(DiseaseStatus.Exposed);
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

}
