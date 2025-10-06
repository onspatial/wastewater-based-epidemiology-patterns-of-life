package pol;

import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;

import pol.environment.AgentMobility;
import pol.log.Characteristics;
import pol.log.ExtLogger;
import pol.log.Skip;

/**
 * General description_________________________________________________________
 * A class to handle defecation need functions such as choosing
 * where to defecate and so on. Each agent has one {@code DefecationNeed}
 * object.
 * 
 * @author Hossein Amiri (hossein.amiri at gmu.edu)
 * 
 */
public class DefecationNeed implements Need, java.io.Serializable {
	private static final long serialVersionUID = 1247800092072951967L;
	private static double MAX_EMPTINESS = 100;
	@Skip
	private double noNeedToDefecate;
	@Skip
	private DefecationStatus status;
	@Skip
	private int numberOfDefecationDone;
	@Skip
	private double noNeedToDefecateReachTimeInMinutes;
	@Skip
	private double keepingNoNeedToDefecateTimeInMinutes;
	@Skip
	private double noNeedToDefecateDecreasePerStep;
	@Skip
	private double noNeedToDefecateIncreasePerStep;
	@Skip
	private double needToDefecateThreshold;
	@Skip
	private double urgentNeedToDefecateThreshold;
	@Characteristics
	private double defecationRate;
	@Skip
	private LocalDateTime lastTimeDefecated;
	@Skip
	private Person agent;
	private final static ExtLogger logger = ExtLogger.create(DefecationNeed.class);

	/**
	 * The constructor to initialize the poop need with the default defecationRate
	 * value.
	 * 
	 * @param agent
	 *              a reference to the agent.
	 */
	public DefecationNeed(Person agent) {
		this(agent, WorldParameters.DEFAULT_DEFECATION_RATE_VALUE);
	}

	/**
	 * The constructor to initialize the poop need with a specific defecationRate
	 * value.
	 * 
	 * @param agent
	 * @param defecationRate
	 */
	public DefecationNeed(Person agent, double defecationRate) {
		this.agent = agent;
		this.noNeedToDefecate = MAX_EMPTINESS;
		this.setDefecationRate(defecationRate);
		numberOfDefecationDone = 0;
		defecated();
	}

	/**
	 * This is how poop need is updated.
	 */
	@Override
	public void update() {
		int minuteDiff = Minutes.minutesBetween(lastTimeDefecated, agent.getSimulationTime()).getMinutes();
		switch (this.status) {
			case JustDefecated:
				this.status = DefecationStatus.NoNeedToDefecate;
				this.noNeedToDefecate = MAX_EMPTINESS;

				break;
			case NoNeedToDefecate:
				if (minuteDiff >= (noNeedToDefecateReachTimeInMinutes + keepingNoNeedToDefecateTimeInMinutes)) {
					this.status = DefecationStatus.BuildingPressure;
				}
				break;
			case BuildingPressure:
				noNeedToDefecate -= noNeedToDefecateDecreasePerStep;
				if (noNeedToDefecate <= needToDefecateThreshold) {
					this.status = DefecationStatus.NeedToDefecate;
				}
				break;
			case NeedToDefecate:
				noNeedToDefecate -= noNeedToDefecateDecreasePerStep;
				noNeedToDefecate = Math.max(noNeedToDefecate, urgentNeedToDefecateThreshold);
				if (noNeedToDefecate <= urgentNeedToDefecateThreshold) {
					this.status = DefecationStatus.UrgentNeedToDefecate;
				}
				break;
			case UrgentNeedToDefecate:
				break;
		}
	}

	@Override
	public void satisfy() {
		PersonMode currentMode = agent.getCurrentMode();
		WorldModel model = agent.getModel();
		AgentMobility mobility = agent.getMobility();
		DailyPlan dailyPlanForToday = agent.getTodaysPlan();
		if (isSatisfied() == false && currentMode != PersonMode.Transport) {
			defecated();
		}

	}

	@Override
	public boolean isSatisfied() {
		return status != DefecationStatus.NeedToDefecate
				&& status != DefecationStatus.UrgentNeedToDefecate;
	}

	public void resetNumberOfDefecationDone() {
		this.numberOfDefecationDone = 0;
	}

	public void defecated() {
		lastTimeDefecated = agent.getSimulationTime();
		this.status = DefecationStatus.JustDefecated;
		noNeedToDefecateIncreasePerStep = (MAX_EMPTINESS - noNeedToDefecate)
				/ (noNeedToDefecateReachTimeInMinutes / (double) this.agent.getModel()
						.getMinutePerStep());
		numberOfDefecationDone++;
	}

	public void kill() {
		this.agent = null;
		this.lastTimeDefecated = null;
	}

	public double getFullness() {
		return noNeedToDefecate;
	}

	/**
	 * 0: doesn't have a big desire for poop. 1: very strong desire for poop.
	 * 
	 * @param defecationRate
	 */
	public void setDefecationRate(double defecationRate) {
		this.defecationRate = defecationRate;
		if (defecationRate > agent.getModel().params.defecationRateUpperBound
				|| defecationRate < agent.getModel().params.defecationRateLowerBound) {
			logger.error("Defecation rate should be within ["
					+ agent.getModel().params.defecationRateLowerBound + ","
					+ agent.getModel().params.defecationRateUpperBound + ")");
			return;
		}
		noNeedToDefecateReachTimeInMinutes = 60 - defecationRate * 30;
		keepingNoNeedToDefecateTimeInMinutes = 180 - defecationRate * 60;
		noNeedToDefecateDecreasePerStep = this.agent.getModel().getMinutePerStep()
				* defecationRate * 0.65;
		needToDefecateThreshold = 30 + 20 * defecationRate;
		urgentNeedToDefecateThreshold = 0;
	}

	public double getDefecationRate() {
		return defecationRate;
	}

	public int getNumberOfDefecationDone() {
		return numberOfDefecationDone;
	}

	public DefecationStatus getStatus() {
		return status;
	}
}
