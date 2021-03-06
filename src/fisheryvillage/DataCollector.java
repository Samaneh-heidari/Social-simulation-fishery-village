package fisheryvillage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import fisheryvillage.common.Constants;
import fisheryvillage.common.HumanUtils;
import fisheryvillage.common.Logger;
import fisheryvillage.common.SimUtils;
import fisheryvillage.population.Human;
import fisheryvillage.population.Resident;
import fisheryvillage.population.Status;
import fisheryvillage.property.Boat;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.space.grid.GridPoint;
import valueframework.AbstractValue;

public class DataCollector {
	
	private int migratedOutSelf;
	private int migratedOutWith;
	private int migratedOutChildren;
	private int migratedIn;
	private int childrenBorn;
	private int died;
	private int migratedOutSelfTick;
	private int migratedOutWithTick;
	
	List<String> migratedPersons;
	List<String> boats;
	List<String> migratedPersonsExt;
	
	public DataCollector(GridPoint location) {
		
		SimUtils.getContext().add(this);
		if (!SimUtils.getGrid().moveTo(this, location.getX(), location.getY())) {
			Logger.logError("DataCollector could not be placed, coordinate: " + location);
		}
		
		migratedOutSelf = 0;
		migratedOutWith = 0;
		migratedOutChildren = 0;
		migratedIn = 0;
		childrenBorn = 0;
		died = 0;
		migratedOutSelfTick = 0;
		migratedOutWithTick = 0;
		
		migratedPersons = new ArrayList<String>();
		migratedPersons.add("Tick,OwnInitiative,id,isMan,age,status,money,p,s,t,u");
		boats = new ArrayList<String>();
		boats.add("Tick,ID,Max cap,Fishers,Has captain,Fish amount,P Thr.,P Lvl.,S Thr., S Lvl.,U Thr.,U Lvl.,T Thr.,T Lvl.,MoneyForExtras");
		migratedPersonsExt = new ArrayList<String>();
		migratedPersonsExt.add("Tick,id,gender,foreigner,hasBeenFisher,age,money,childrenWanted,nettoIncome,necessaryCost,jobTitle,status,workplaceId,notHappyTick,migrTickRequired,socialStatus" +
			 	  ",partnerId,salaryTaxed,hasEnoughMoney,childrenUnder18,house,P Thr.,P Lvl.,S Thr., S Lvl.,U Thr.,U Lvl.,T Thr.,T Lvl.,s_job,s_house,s_boat,s_ecol,s_econ,s_don,s_events,s_free_ev");
	}
	
	public void addMigratedPersonsExt(boolean initiative, String datum) {
		migratedPersonsExt.add(initiative + "," + datum);
	}
	
	public void saveMigrationData() {
		writeToFile("D:\\UniversiteitUtrecht\\7MasterThesis\\Repast-filesink\\fisheryvillage\\MigrationData.txt", migratedPersons);
		writeToFile("D:\\UniversiteitUtrecht\\7MasterThesis\\Repast-filesink\\fisheryvillage\\BoatData.txt", boats);
		writeToFile("D:\\UniversiteitUtrecht\\7MasterThesis\\Repast-filesink\\fisheryvillage\\MigrationDataExt.txt", migratedPersonsExt);
	}
	
	public void writeToFile(String filePathAndName, List<String> data) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(filePathAndName, "UTF-8");
			for (String datum : data) {
				writer.println(datum);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stepReset() {
		migratedOutSelfTick = 0;
		migratedOutWithTick = 0;
	}
	
	public void addFishingData(int id, String datum) {
		int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		boats.add(tick + "," + id + "," + datum);
	}
	
	public void addMigratorOut(boolean ownInitiative, int id) {
		
		Resident r = HumanUtils.getResidentById(id);
		if (r == null)
			return ;
		
		addMigrator(ownInitiative, r.getId(), r.isMan(), r.getAge(), r.getStatus().name(), r.getMoney(), r.getThreshold(AbstractValue.POWER),
				r.getThreshold(AbstractValue.SELFDIRECTION), r.getThreshold(AbstractValue.TRADITION), r.getThreshold(AbstractValue.UNIVERSALISM));
	}

	public void addMigrator(boolean ownInitiative, int id, boolean isMan, int age, String status, double money, double p, double s, double t, double u) {
		
		int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		String string = tick + "," + ownInitiative + "," + id + "," + isMan + "," + age + "," + status + "," + money + "," + p + "," + s + "," + t + "," + u;
		migratedPersons.add(string);
		
		if (ownInitiative) {
			migratedOutSelf ++;
			migratedOutSelfTick ++;
		}
		else {
			if (age < Constants.HUMAN_ADULT_AGE) {
				migratedOutChildren ++;
			}
			else {
				migratedOutWith ++;
				migratedOutWithTick ++;
			}
		}
	}

	public void addChildBorn() {
		childrenBorn ++;
	}
	
	public void addMigratedIn() {
		migratedIn ++;
	}
	
	public void addDied() {
		died ++;
	}
	
	public int getMigratedOutSelf() {
		return migratedOutSelf;
	}
	
	public int getMigratedOutWith() {
		return migratedOutWith;
	}
	
	public int getMigratedOutChildren() {
		return migratedOutChildren;
	}
	
	public int getMigratedOutTotal() {
		return migratedOutSelf + migratedOutWith + migratedOutChildren;
	}
	
	public int getMigratedOutSelfTick() {
		return migratedOutSelfTick;
	}
	
	public int getMigratedOutWithTick() {
		return migratedOutWithTick;
	}
	
	public int getMigratedIn() {
		return migratedIn;
	}
	
	public int getChildrenBorn() {
		return childrenBorn;
	}

	public int getPopulationNumber() {
		return SimUtils.getCouncil().getNumberOfPeople();
	}
	
	public int getDied() {
		return died;
	}
	
	public double getFactoryWealth() {
		
		return SimUtils.getFactory().getSavings();
	}
	
	public double getAdultAndElderlyWealthAvg() {
		
		ArrayList<Human> humans = SimUtils.getObjectsAll(Human.class);
		int count = 0;
		double money = 0;
		for (Human human : humans) {
			if (human.getAge() >= Constants.HUMAN_ADULT_AGE && human.getAge() < Constants.HUMAN_ELDERLY_CARE_AGE) {
				count ++;
				money += human.getMoney();
			}
		}
		if (count >= 1)
			return money / count;
		else
			return 0;
	}

	/**
	 * Average satisfied values times 0.25 which means the percentage of satisfied values is shown
	 * @param ageMin
	 * @param ageMaxAddOne
	 * @return
	 */
	public double averageSatisfiedValues(int ageMin, int ageMaxAddOne, Status status) {
		
		int valuesSatisfied = 0;
		int count = 0;
		for (Resident resident : SimUtils.getObjectsAll(Resident.class)) {
			if (resident.getAge() >= ageMin && resident.getAge() < ageMaxAddOne && (status == Status.NONE || resident.getStatus() == status)) {
				valuesSatisfied += resident.getSatisfiedValuesCount();
				count ++;
			}
		}
		
		if (count > 0) {
			return (((double) valuesSatisfied) / count) * 0.25;
		}
		else {
			return -0.05;
		}
	}

	public double getAverageSatisfiedValuesElderly() {
		return averageSatisfiedValues(Constants.HUMAN_ELDERLY_AGE, Constants.HUMAN_ELDERLY_CARE_AGE, Status.NONE);
	}
	
	public double getAverageSatisfiedValuesAdult() {
		return averageSatisfiedValues(Constants.HUMAN_ADULT_AGE, Constants.HUMAN_ELDERLY_AGE, Status.NONE);
	}
	
	public double getAverageSatisfiedValuesAdultElderly() {
		return averageSatisfiedValues(Constants.HUMAN_ADULT_AGE, Constants.HUMAN_ELDERLY_CARE_AGE, Status.NONE);
	}
	
	public double getAverageSatisfiedValuesCaptain() {
		return averageSatisfiedValues(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.CAPTAIN);
	}
	
	public double getAverageSatisfiedValuesCaretaker() {
		return averageSatisfiedValues(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.ELDERLY_CARETAKER);
	}
	
	public double getAverageSatisfiedValuesBoss() {
		return averageSatisfiedValues(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.FACTORY_BOSS);
	}
	
	public double getAverageSatisfiedValuesWorker() {
		return averageSatisfiedValues(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.FACTORY_WORKER);
	}
	
	public double getAverageSatisfiedValuesFisher() {
		return averageSatisfiedValues(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.FISHER);
	}
	
	public double getAverageSatisfiedValuesMayor() {
		return averageSatisfiedValues(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.MAYOR);
	}

	public double getAverageSatisfiedValuesTeacher() {
		return averageSatisfiedValues(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.TEACHER);
	}
	
	public double getAverageSatisfiedValuesUnemployed() {
		return averageSatisfiedValues(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.UNEMPLOYED);
	}
	
	public double getAverageSatisfiedValuesWorkOutOfTown() {
		return averageSatisfiedValues(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.WORK_OUT_OF_TOWN);
	}
	
	public double averageSocialStatus(int ageMin, int ageMaxAddOne, Status status) {
		
		double valuesSatisfied = 0;
		int count = 0;
		for (Resident resident : SimUtils.getObjectsAll(Resident.class)) {
			if (resident.getAge() >= ageMin && resident.getAge() < ageMaxAddOne && (status == Status.NONE || resident.getStatus() == status)) {
				valuesSatisfied += resident.getSocialStatusValue();
				count ++;
			}
		}
		
		if (count > 0) {
			return valuesSatisfied / count;
		}
		else {
			return -0.05;
		}
	}

	public double getAverageSocialStatusElderly() {
		return averageSocialStatus(Constants.HUMAN_ELDERLY_AGE, Constants.HUMAN_ELDERLY_CARE_AGE, Status.NONE);
	}
	
	public double getAverageSocialStatusAdult() {
		return averageSocialStatus(Constants.HUMAN_ADULT_AGE, Constants.HUMAN_ELDERLY_AGE, Status.NONE);
	}
	
	public double getAverageSocialStatusAdultElderly() {
		return averageSocialStatus(Constants.HUMAN_ADULT_AGE, Constants.HUMAN_ELDERLY_CARE_AGE, Status.NONE);
	}
	
	public double getAverageSocialStatusCaptain() {
		return averageSocialStatus(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.CAPTAIN);
	}
	
	public double getAverageSocialStatusCaretaker() {
		return averageSocialStatus(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.ELDERLY_CARETAKER);
	}
	
	public double getAverageSocialStatusBoss() {
		return averageSocialStatus(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.FACTORY_BOSS);
	}
	
	public double getAverageSocialStatusWorker() {
		return averageSocialStatus(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.FACTORY_WORKER);
	}
	
	public double getAverageSocialStatusFisher() {
		return averageSocialStatus(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.FISHER);
	}
	
	public double getAverageSocialStatusMayor() {
		return averageSocialStatus(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.MAYOR);
	}

	public double getAverageSocialStatusTeacher() {
		return averageSocialStatus(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.TEACHER);
	}
	
	public double getAverageSocialStatusUnemployed() {
		return averageSocialStatus(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.UNEMPLOYED);
	}
	
	public double getAverageSocialStatusWorkOutOfTown() {
		return averageSocialStatus(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.WORK_OUT_OF_TOWN);
	}
	
	public double happyPercentage(int ageMin, int ageMaxAddOne, Status status) {
		
		int happy = 0;
		int count = 0;
		for (Resident resident : SimUtils.getObjectsAll(Resident.class)) {
			if (resident.getAge() >= ageMin && resident.getAge() < ageMaxAddOne && (status == Status.NONE || resident.getStatus() == status)) {
				count ++;
				if (resident.getIsHappy()) {
					happy ++;
				}
			}
		}
		if (count > 0) {
			return ((double) happy) / count;
		}
		else {
			return -0.05;
		}
	}
	
	public double notHappyPercentage(int ageMin, int ageMaxAddOne, Status status) {
		
		int notHappy = 0;
		int count = 0;
		for (Resident resident : SimUtils.getObjectsAll(Resident.class)) {
			if (resident.getAge() >= ageMin && resident.getAge() < ageMaxAddOne && (status == Status.NONE || resident.getStatus() == status)) {
				count ++;
				if (!resident.getIsHappy()) {
					notHappy ++;
				}
			}
		}
		return ((double) notHappy) / count;
	}
	
	public double getHappyPercentageAdult() {
		return happyPercentage(Constants.HUMAN_ADULT_AGE, Constants.HUMAN_ELDERLY_AGE, Status.NONE);
	}
	
	public double getHappyPercentageElderly() {
		return happyPercentage(Constants.HUMAN_ELDERLY_AGE, Constants.HUMAN_ELDERLY_CARE_AGE, Status.NONE);
	}
	
	public double getHappyPercentageAdultElderly() {
		return happyPercentage(Constants.HUMAN_ADULT_AGE, Constants.HUMAN_ELDERLY_CARE_AGE, Status.NONE);
	}
	
	public double getHappyPercentageCaptain() {
		return happyPercentage(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.CAPTAIN);
	}
	
	public double getHappyPercentageCaretaker() {
		return happyPercentage(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.ELDERLY_CARETAKER);
	}
	
	public double getHappyPercentageBoss() {
		return happyPercentage(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.FACTORY_BOSS);
	}
	
	public double getHappyPercentageWorker() {
		return happyPercentage(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.FACTORY_WORKER);
	}
	
	public double getHappyPercentageFisher() {
		return happyPercentage(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.FISHER);
	}
	
	public double getHappyPercentageMayor() {
		return happyPercentage(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.MAYOR);
	}

	public double getHappyPercentageTeacher() {
		return happyPercentage(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.TEACHER);
	}
	
	public double getHappyPercentageUnemployed() {
		return happyPercentage(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.UNEMPLOYED);
	}
	
	public double getHappyPercentageWorkOutOfTown() {
		return happyPercentage(0, Constants.HUMAN_ELDERLY_CARE_AGE, Status.WORK_OUT_OF_TOWN);
	}

	/**
	 * Retrieves a distribution
	 * @param number
	 * @return
	 */
	public double satisfiedValuesCount(int number) {
		int total = 0;
		int count = 0;
		for (Resident resident : SimUtils.getObjectsAll(Resident.class)) {
			if (resident.getAge() >= Constants.HUMAN_ADULT_AGE && resident.getAge() < Constants.HUMAN_ELDERLY_AGE) {
				total ++;
				if (resident.getSatisfiedValuesCount() == number) {
					count ++;
				}
			}
		}
		return ((double) count) / total;
	}
	
	public double getSatisfiedValues0() {
		return satisfiedValuesCount(0);
	}

	public double getSatisfiedValues1() {
		return satisfiedValuesCount(1);
	}

	public double getSatisfiedValues2() {
		return satisfiedValuesCount(2);
	}
	
	public double getSatisfiedValues3() {
		return satisfiedValuesCount(3);
	}
	
	public double getSatisfiedValues4() {
		return satisfiedValuesCount(4);
	}
	
	public String getLabel() {
		
		return "DataCollector";
	}
	
	public double donationPercentageDonate() {
		//int total = 0;
		int count = 0;
		for (Resident resident : SimUtils.getObjectsAll(Resident.class)) {
			if (resident.getAge() >= Constants.HUMAN_ADULT_AGE && resident.getAge() < Constants.HUMAN_ELDERLY_AGE) {
				if (resident.getGraphDonateType() != -1) {
					//total ++;
					if (resident.getGraphDonateType() == 2) {
						count ++;
					}
				}
			}
		}
		return count; //((double) count) / total;
	}
	
	public double donationPercentageNoDonate() {
		//int total = 0;
		int count = 0;
		for (Resident resident : SimUtils.getObjectsAll(Resident.class)) {
			if (resident.getAge() >= Constants.HUMAN_ADULT_AGE && resident.getAge() < Constants.HUMAN_ELDERLY_AGE) {
				if (resident.getGraphDonateType() != -1) {
					//total ++;
					if (resident.getGraphDonateType() == 1) {
						count ++;
					}
				}
			}
		}
		return count; //((double) count) / total;
	}

	public double donationPercentageNoMoney() {
		//int total = 0;
		int count = 0;
		for (Resident resident : SimUtils.getObjectsAll(Resident.class)) {
			if (resident.getAge() >= Constants.HUMAN_ADULT_AGE && resident.getAge() < Constants.HUMAN_ELDERLY_AGE) {
				if (resident.getGraphDonateType() != -1) {
					//total ++;
					if (resident.getGraphDonateType() == 0) {
						count ++;
					}
				}
			}
		}
		return count; //((double) count) / total;
	}
	
	public double eventPercentage(int type) {
		//int total = 0;
		int count = 0;
		for (Resident resident : SimUtils.getObjectsAll(Resident.class)) {
			if (resident.getAge() >= Constants.HUMAN_ADULT_AGE && resident.getAge() < Constants.HUMAN_ELDERLY_AGE) {
				//total ++;
				if (resident.getGraphEventType() == type) {
					count ++;
				}
			}
		}
		return count; //((double) count) / total;
	}
	
	public double eventNoMoney() {

		return eventPercentage(0);
	}
	
	public double eventOrgFree() {

		return eventPercentage(1);
	}

	public double eventOrgCom() {

		return eventPercentage(2);
	}
	
	public double eventAttFree() {

		return eventPercentage(3);
	}

	public double eventAttCom() {

		return eventPercentage(4);
	}
	
	public Boat getBoatById(int id) {
		
		ArrayList<Boat> boats = SimUtils.getObjectsAll(Boat.class);
		for (Boat boat : boats) {
			if (boat.getId() == id)
				return boat;
		}
		return null;
	}
	
	public int getBoat1EmployeeCap() {
		return getBoatById(31).getBoatType().getEmployeeCapacity();
	}
	
	public int getBoat1FisherCount() {
		return getBoatById(31).getFisherCount();
	}
	
	public int getBoat1HasCaptain() {
		return getBoatById(31).getCaptainIndex();
	}
	
	public double getBoat1FishToCatchNorm() {
		return getBoatById(31).getNormalizedFishToCatchPerPerson();
	}
	
	public int getBoat2EmployeeCap() {
		return getBoatById(32).getBoatType().getEmployeeCapacity();
	}

	public int getBoat2FisherCount() {
		return getBoatById(32).getFisherCount();
	}
	
	public int getBoat2HasCaptain() {
		return getBoatById(32).getCaptainIndex();
	}
	
	public double getBoat2FishToCatchNorm() {
		return getBoatById(32).getNormalizedFishToCatchPerPerson();
	}
}
