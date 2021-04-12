package abseir_family.dynamics;

import abseir_family.GGmodel_builder;
import abseir_family.agents.Individual;
import abseir_family.agents.Family;
import abseir_family.agents.ImmunizationCenter;
import abseir_family.utils.NumberOfNeverInfectedComparator;

import repast.simphony.context.Context;
import repast.simphony.util.collections.IndexedIterable;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.engine.schedule.DefaultActionFactory;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.random.RandomHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

public class GGmodel_scheduler{
	public IndexedIterable<Object> individualsList,familiesList;
	public Context<Object> ggmodelContext;
	ScheduleParameters scheduleParameters;
	DefaultActionFactory statActionFactory;
	IAction statAction;
	ImmunizationCenter immunizationCenter;
	public ArrayList<Individual> schedulerIndividualsList;
	public ArrayList<Family> schedulerFamiliesList;
	public ArrayList<Family> caseZeroFamiliesList;
	
	public GGmodel_scheduler(Context<Object> theContext,ArrayList<Individual> allIndividualsList,ArrayList<Family> allFamiliesList){
		ggmodelContext=theContext;
		try{
			individualsList=ggmodelContext.getObjects(Class.forName("abseir_family.agents.Individual"));
			familiesList=ggmodelContext.getObjects(Class.forName("abseir_family.agents.Family"));
		}
		catch(ClassNotFoundException e){
			System.out.println("Class not found");
		}
		statActionFactory = new DefaultActionFactory();
		immunizationCenter=new ImmunizationCenter(allIndividualsList,allFamiliesList);
		schedulerIndividualsList=new ArrayList<Individual>(allIndividualsList);
		schedulerFamiliesList=new ArrayList<Family>(allFamiliesList);
		caseZeroFamiliesList=new ArrayList<Family>();
	}
	public void scheduleEvents(){
		
		scheduleParameters=ScheduleParameters.createOneTime(GGmodel_builder.caseZetoTime,0.0);
//		scheduleParameters=ScheduleParameters.createRepeating(GGmodel_builder.caseZetoTime,1,0.0);
		GGmodel_builder.schedule.schedule(scheduleParameters,this,"scheduleCaseZero");
		
		scheduleParameters=ScheduleParameters.createOneTime(GGmodel_builder.swabTestAndQuarantineStartingTime,40.0);
		GGmodel_builder.schedule.schedule(scheduleParameters,this,"scheduleSetIndividualsSymtomsAge");

		scheduleParameters=ScheduleParameters.createRepeating(1,1,39.0);
		GGmodel_builder.schedule.schedule(scheduleParameters,this,"scheduleStepIndividualsInfectionAge");
		
		scheduleParameters=ScheduleParameters.createRepeating(1,1,38.0);
		GGmodel_builder.schedule.schedule(scheduleParameters,this,"scheduleStepIndividualsQuarantineAge");

		scheduleParameters=ScheduleParameters.createRepeating(1,1,37.0);
		GGmodel_builder.schedule.schedule(scheduleParameters,this,"scheduleStepContacts");

		if(GGmodel_builder.individualsBasedInnumization){
			scheduleParameters=ScheduleParameters.createRepeating(GGmodel_builder.immunizationStartingTime,1,36.0);
			GGmodel_builder.schedule.schedule(scheduleParameters,this,"scheduleUpdateImmunizationLists");

			scheduleParameters=ScheduleParameters.createRepeating(GGmodel_builder.immunizationStartingTime,1,35.0);
			GGmodel_builder.schedule.schedule(scheduleParameters,this,"scheduleUpdateAvailableImmunizationDoses");

			scheduleParameters=ScheduleParameters.createRepeating(GGmodel_builder.immunizationStartingTime,1,35.0);
			GGmodel_builder.schedule.schedule(scheduleParameters,this,"scheduleImmunizeIndividuals");
		}
		else{
			scheduleParameters=ScheduleParameters.createRepeating(GGmodel_builder.immunizationStartingTime,1,36.0);
			GGmodel_builder.schedule.schedule(scheduleParameters,this,"scheduleUpdateFamiliesImmunizationLists");

			scheduleParameters=ScheduleParameters.createRepeating(GGmodel_builder.immunizationStartingTime,1,35.0);
			GGmodel_builder.schedule.schedule(scheduleParameters,this,"scheduleUpdateAvailableImmunizationDoses");

			scheduleParameters=ScheduleParameters.createRepeating(GGmodel_builder.immunizationStartingTime,1,35.0);
			GGmodel_builder.schedule.schedule(scheduleParameters,this,"scheduleImmunizeFamilies");		
		}

	}
	public void scheduleCaseZero(){
		if(GGmodel_builder.verboseFlag){
			System.out.println();
			System.out.println("STARTING INFECTION: CASE ZERO");
		}
		Family aFamily;
		Individual anIndividual;

for(Family thisFamily : schedulerFamiliesList){
			thisFamily.updateState();
			if(thisFamily.getNumberOfNeverInfectedNotImmunized()==GGmodel_builder.numberOfSusceptibleInCaseZeroFamily){
				caseZeroFamiliesList.add(thisFamily);
			}
		}
/*
		boolean notYetInfected=true;
		int runnersOnFamilies=0;
		while(notYetInfected && runnersOnFamilies<caseZeroFamiliesList.size()){
			aFamily=caseZeroFamiliesList.get(runnersOnFamilies);
			notYetInfected=aFamily.performFirstInfection();
			runnersOnFamilies++;
		}
*/
//		System.out.println(caseZeroFamiliesList.size());
	
		Collections.sort(schedulerFamiliesList,new NumberOfNeverInfectedComparator());
		Collections.reverse(schedulerFamiliesList);
		if(GGmodel_builder.verboseFlag){
		for(Family thisFamily : schedulerFamiliesList){
			thisFamily.printInfo();
		}
		}
		boolean notYetInfected=true;
		int runnersOnFamilies=0;
		while(notYetInfected && runnersOnFamilies<schedulerFamiliesList.size()){
			aFamily=schedulerFamiliesList.get(runnersOnFamilies);
			notYetInfected=aFamily.performFirstInfection();
			runnersOnFamilies++;
		}

		
	}
	public void scheduleSetIndividualsSymtomsAge(){
		statAction=statActionFactory.createActionForIterable(individualsList,"setSymptomsAge",false);
		statAction.execute();
		double positionInListOfAllIndividuals=0;
		for(int i=0;i<GGmodel_builder.nOfAsymptomatic;i++){
			GGmodel_builder.allIndividualsList.get((int)positionInListOfAllIndividuals).setAsymptomatic();
			positionInListOfAllIndividuals+=GGmodel_builder.oneAsymptomaticEach;
		}
	}
	public void scheduleStepIndividualsInfectionAge(){
		if(GGmodel_builder.verboseFlag){
			System.out.println();
			System.out.println("INDIVIDUALS: STEP INFECTION AGE");
		}

		statAction=statActionFactory.createActionForIterable(individualsList,"stepInfectionAge",false);
		statAction.execute();
	}
	public void scheduleStepIndividualsQuarantineAge(){
		if(GGmodel_builder.verboseFlag){
			System.out.println();
			System.out.println("INDIVIDUALS: STEP QUARATNINE AGE");
		}

		statAction=statActionFactory.createActionForIterable(individualsList,"stepQuarantineAge",false);
		statAction.execute();
	}

	public void scheduleStepContacts(){
		if(GGmodel_builder.verboseFlag){
			System.out.println("--------------");
			System.out.println();
			System.out.println("INDIVIDUALS: CONTACTS");
		}

		statAction=statActionFactory.createActionForIterable(individualsList,"performContacts",false);
		statAction.execute();
	}
	public void scheduleUpdateImmunizationLists(){
		if(GGmodel_builder.verboseFlag){System.out.println();System.out.println("Immunization centre: update individuals list");}
		immunizationCenter.updateIndividualsLists();
	}
	public void scheduleUpdateAvailableImmunizationDoses(){
		if(GGmodel_builder.verboseFlag){System.out.println();System.out.println("Immunization centre: icrease doses");}
		immunizationCenter.increaseAvailableImmunizationDoses();
	}
	public void scheduleImmunizeIndividuals(){
		if(GGmodel_builder.verboseFlag){System.out.println();System.out.println("Immunization centre: perform individuals immunization");}
		immunizationCenter.performIndividualsImmunization();
	}
	public void scheduleUpdateFamiliesImmunizationLists(){
		if(GGmodel_builder.verboseFlag){System.out.println();System.out.println("Immunization centre: update families list");}
		immunizationCenter.updateFamiliesLists();
	}
	public void scheduleImmunizeFamilies(){
		if(GGmodel_builder.verboseFlag){System.out.println();System.out.println("Immunization centre: perform families immunization");}
		immunizationCenter.performFamiliesImmunization();
	}

}
