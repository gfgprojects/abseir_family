package abseir_family.agents;

import abseir_family.GGmodel_builder;
import abseir_family.agents.Individual;

import repast.simphony.random.RandomHelper;

import java.util.ArrayList;
import java.util.Collections;
//import randomwalker.utils.PositionAverageAggregateDataSource;

//import repast.simphony.random.RandomHelper;
//import repast.simphony.engine.schedule.ScheduledMethod;
//import repast.simphony.essentials.RepastEssentials;
public class Individual {
	public boolean wasInfected=false;
	public boolean justInfected=false;
	public boolean immunizationState=false;
	double myFragility;
	public int infectionAge=0;
	int stopContagionAtInfectionAge=GGmodel_builder.recoveryTimeInAsymptomatics;
	public int myMaxNumberOfContatsOutOfFamily=RandomHelper.nextIntFromTo(GGmodel_builder.minNumberOfContactsOutOfFamily,GGmodel_builder.maxNumberOfContactsOutOfFamily);
	int tmpNumberOfContatsOutOfFamily;
	int tmpPerformedContacts;
	boolean quarantineState=false;
	public int quarantineAge=0;
	int quarantineLength=2;
	int myGlobalID,myIDInFamily,myFamilyID,myNumberOfRelatives;
	ArrayList<Individual> myRelativesList;
	Individual tmpIndividual;

	public Individual(int myglobalid, int myidinfamily,int myfamilyid){
		myGlobalID=myglobalid;
		myIDInFamily=myidinfamily;
		myFamilyID=myfamilyid;
		myFragility=RandomHelper.nextDouble();
		if(GGmodel_builder.verboseFlag){
			System.out.println(" individual created "+myGlobalID+", position in family "+myIDInFamily+", family "+myFamilyID+" fragility "+myFragility);
		}
	}
	public void setRelatives(ArrayList<Individual> relativesList){
//		Collections.copy(relativesList,myRelativesList);
		myRelativesList=new ArrayList<Individual>(relativesList);
		myRelativesList.remove(myIDInFamily);
		myNumberOfRelatives=myRelativesList.size();
		if(GGmodel_builder.verboseFlag){
			System.out.println("  individual "+myGlobalID+", position in family "+myIDInFamily+", family "+myFamilyID+" n of relatives "+myNumberOfRelatives);
		}

	}
	public void performContacts(){

		double tmpRand;
		if(infectionAge>GGmodel_builder.latencyTime && quarantineAge==0){
			if(GGmodel_builder.verboseFlag){System.out.println(" contacting individual "+myGlobalID+", position in family: "+myIDInFamily+", family: "+myFamilyID+" n of relatives "+myNumberOfRelatives+" max n contacts "+myMaxNumberOfContatsOutOfFamily+" justInfected: "+justInfected+" was infected: "+wasInfected+" immune: "+immunizationState+" fragility: "+myFragility);}
			//contats in family
			for(int i=0;i<myRelativesList.size();i++){
				tmpRand=RandomHelper.nextDouble();
			if(GGmodel_builder.verboseFlag){System.out.println("   IN: individual "+myGlobalID+" family "+myFamilyID+" rnd "+tmpRand);}
				if(tmpRand<GGmodel_builder.contagionProbabilityInFamily){
					myRelativesList.get(i).setInfection();
				}
			}
			//contacts out of family
			tmpNumberOfContatsOutOfFamily=myMaxNumberOfContatsOutOfFamily;
//			tmpNumberOfContatsOutOfFamily=RandomHelper.nextIntFromTo(1,myMaxNumberOfContatsOutOfFamily);
//			System.out.println("individual "+myGlobalID+" this time trials "+tmpNumberOfContatsOutOfFamily);
			tmpPerformedContacts=0;
			while(tmpPerformedContacts<tmpNumberOfContatsOutOfFamily){
//			System.out.println("individual "+myGlobalID+" others");
				tmpIndividual=GGmodel_builder.allIndividualsList.get(RandomHelper.nextIntFromTo(0,(GGmodel_builder.allIndividualsList.size()-1)));
				if(tmpIndividual.getMyFamilyID()!=myFamilyID){
					tmpRand=RandomHelper.nextDouble();
if(GGmodel_builder.verboseFlag){System.out.println("   OU trial "+tmpRand);} 
					if(tmpRand<GGmodel_builder.contagionProbabilityOutOfFamily){
						tmpIndividual.setInfectionFromOutOfFamily();
					}
					tmpPerformedContacts++;
				}
			}
		}
	}
	public void setInfection(){ 
		if(quarantineState){
			if(GGmodel_builder.verboseFlag){
				System.out.println("     IN: CANNOT BE REACHED BECAUSE OF QUARANTINE individual "+myGlobalID+", position in family "+myIDInFamily+", family "+myFamilyID+" new infection "+justInfected);
			}
		}
		else{
			if(infectionAge==0){
				justInfected=true;
				wasInfected=true;
				if(GGmodel_builder.verboseFlag){
					System.out.println("     IN: UNFORTUNATELY I GOT IT individual "+myGlobalID+", position in family "+myIDInFamily+", family "+myFamilyID+" n of relatives "+myNumberOfRelatives+" new infection "+justInfected);
				}
			}
			else{
				if(infectionAge>0){
					if(GGmodel_builder.verboseFlag){
						System.out.println("     IN: MY INFECTION IS RUNNING individual "+myGlobalID+", position in family "+myIDInFamily+", family "+myFamilyID+" new infection "+justInfected);
					}
				}
				if(infectionAge<0){
					if(GGmodel_builder.verboseFlag){
						System.out.println("     IN: I AM IMMUNE individual "+myGlobalID+", position in family "+myIDInFamily+", family "+myFamilyID+" new infection "+justInfected);
					}
				}
			}
		}
	}
	public void setInfectionFromOutOfFamily(){ 
		if(quarantineState){
			if(GGmodel_builder.verboseFlag){
				System.out.println("  CANNOT BE REACHED BECAUSE OF QUARANTINE individual "+myGlobalID+", position in family "+myIDInFamily+", family "+myFamilyID+" new infection "+justInfected);
			}
		}
		else{
			if(infectionAge==0){
				justInfected=true;
				wasInfected=true;
				if(GGmodel_builder.verboseFlag){
					System.out.println("     OUT: UNFORTUNATELY I GOT IT individual "+myGlobalID+", position in family "+myIDInFamily+", family "+myFamilyID+" n of relatives "+myNumberOfRelatives+" new infection "+justInfected);
				}
			}
			else{
				if(infectionAge>0){
					if(GGmodel_builder.verboseFlag){
						System.out.println("  MY INFECTION IS RUNNING individual "+myGlobalID+", position in family "+myIDInFamily+", family "+myFamilyID+" new infection "+justInfected);
					}
				}
				if(infectionAge<0){
					if(GGmodel_builder.verboseFlag){
						System.out.println("  I AM IMMUNE individual "+myGlobalID+", position in family "+myIDInFamily+", family "+myFamilyID+" new infection "+justInfected);
					}
				}
			}
		}
	}

	public void stepInfectionAge(){
		if(infectionAge != 0){
			infectionAge++;
		}
		if(justInfected){
			infectionAge=1;
			justInfected=false;
		}
		if(infectionAge>GGmodel_builder.recoveryTimeInAsymptomatics){
			infectionAge=GGmodel_builder.immunityAfterFirstInfection;
		}
		else{
			if(infectionAge>stopContagionAtInfectionAge){
				quarantineState=true;
				for(int i=0;i<myRelativesList.size();i++){
					myRelativesList.get(i).setQuarantineBecauseOfOneOfMyRelativesInfection();
				}
			}
		}
	}
	public void stepQuarantineAge(){
		if(quarantineState){
			quarantineAge++;
		}
		if(quarantineAge>GGmodel_builder.quarantineLength){
			quarantineState=false;
			quarantineAge=0;
		}
		if(GGmodel_builder.verboseFlag){System.out.println("      individual "+myGlobalID+", position in family "+myIDInFamily+", family "+myFamilyID+" infectionAge "+infectionAge+" quarantine "+quarantineState+" quarantine time "+quarantineAge);}
	}
	public void setQuarantineBecauseOfOneOfMyRelativesInfection(){
		if(infectionAge>=0){
			quarantineState=true;
		}
	}

	public void setSymptomsAge(){
		stopContagionAtInfectionAge=RandomHelper.nextIntFromTo(GGmodel_builder.minimumDiscoveryTimeInInfected,GGmodel_builder.recoveryTimeInAsymptomatics);
	}
	public void setAsymptomatic(){
		stopContagionAtInfectionAge=GGmodel_builder.recoveryTimeInAsymptomatics+1;
		if(GGmodel_builder.verboseFlag){System.out.println("  set as asymptomatic "+myGlobalID+", position in family "+myIDInFamily+", family "+myFamilyID+" infectionAge "+infectionAge+" quarantine "+quarantineState+" quarantine time "+quarantineAge);}
	}
	public int getMyFamilyID(){
		return myFamilyID;
	}
	public boolean getWasInfected(){
		return wasInfected;
	}
	public double getFragility(){
		return myFragility;
	}
	public int getMyMaxNumberOfContatsOutOfFamily(){
		return myMaxNumberOfContatsOutOfFamily;
	}
	public void setMyMaxNumberOfContatsOutOfFamily(int thiIndividualMaxNumberOfContactsOutOfFamily){
		myMaxNumberOfContatsOutOfFamily=thiIndividualMaxNumberOfContactsOutOfFamily;
	}
	public void setImmunization(){
		immunizationState=true;
		infectionAge=GGmodel_builder.immunityAfterImmunization;
	}
	public boolean getImmunization(){
		return immunizationState;
	}
	public boolean getImmunizationAndInfection(){
		return immunizationState && wasInfected;
	}
	public int getInfectionAge(){
		return infectionAge;
	}
	public void printInfo(){
			System.out.println("      individual "+myGlobalID+", position in family: "+myIDInFamily+", family: "+myFamilyID+" n of relatives "+myNumberOfRelatives+" max n contacts "+myMaxNumberOfContatsOutOfFamily+" justInfected: "+justInfected+" was infected: "+wasInfected+" immune: "+immunizationState+" fragility: "+myFragility);
	}
}
