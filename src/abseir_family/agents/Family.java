package abseir_family.agents;

import abseir_family.GGmodel_builder;
import abseir_family.agents.Individual;

import java.util.ArrayList;

//import randomwalker.utils.PositionAverageAggregateDataSource;

//import repast.simphony.random.RandomHelper;
//import repast.simphony.engine.schedule.ScheduledMethod;
//import repast.simphony.essentials.RepastEssentials;
public class Family {
	public int myID;
	public int numberOfIndividuals,numberOfWasInfected,numberOfWasInfectedImmunized,numberOfWasInfectedNotImmunized,numberOfNeverInfected,numberOfNeverInfectedImmunized,numberOfNeverInfectedNotImmunized;
	Individual tmpIndividual;
	ArrayList<Individual> familyMembersList=new ArrayList<Individual>();
	public Family(int myid){
		myID=myid;
		if(GGmodel_builder.verboseFlag){System.out.println("Family created id "+myID);}
	}
	public void addMember(Individual tmpMember){
		familyMembersList.add(tmpMember);
		if(GGmodel_builder.verboseFlag){System.out.println("  Member added: family size "+familyMembersList.size());}
	}
	public void setIndividualsFamily(){
		numberOfIndividuals=familyMembersList.size();
		for(int i=0;i<numberOfIndividuals;i++){
			familyMembersList.get(i).setRelatives(familyMembersList);
		}
	}
	public void updateState(){
		numberOfWasInfected=0;
		numberOfWasInfectedImmunized=0;
		numberOfWasInfectedNotImmunized=0;
		numberOfNeverInfected=0;
		numberOfNeverInfectedImmunized=0;
		numberOfNeverInfectedNotImmunized=0;
		for(Individual anIndividual : familyMembersList){
			if(anIndividual.getWasInfected()){
				numberOfWasInfected++;
				if(anIndividual.getImmunization()){
					numberOfWasInfectedImmunized++;
				}
				else{
					numberOfWasInfectedNotImmunized++;
				}
			}else{
				numberOfNeverInfected++;
				if(anIndividual.getImmunization()){
					numberOfNeverInfectedImmunized++;
				}
				else{
					numberOfNeverInfectedNotImmunized++;
				}
			}
		}
	}
	public int getNumberOfNeverInfected(){
		return numberOfNeverInfected;
	}
	public int getNumberOfNeverInfectedNotImmunized(){
		return numberOfNeverInfectedNotImmunized;
	}

	public void performMembersImmunization(){
		for(Individual anIndividual : familyMembersList){
			if(!anIndividual.getImmunization()){
				if(!anIndividual.getWasInfected()){
					anIndividual.setImmunization();
				}
			}
		}
	}
	public boolean performFirstInfection(){
		Individual anIndividual;
		boolean notYetInfected=true;
		int runnerOnMembers=0;
		while(notYetInfected && runnerOnMembers<familyMembersList.size()){
			anIndividual=familyMembersList.get(runnerOnMembers);
			if(anIndividual.getInfectionAge()==0){
				anIndividual.setInfection();
				anIndividual.setMyMaxNumberOfContatsOutOfFamily(GGmodel_builder.caseZeroNumberOfContactsOutOfFamily);
//				System.out.println("FIRST INFECTION");
//				anIndividual.printInfo();
				notYetInfected=false;
			}
			runnerOnMembers++;
		}
		return notYetInfected;
	}
	public void printInfo(){
		System.out.println("Family id "+myID+" components: "+numberOfIndividuals+" was infect: "+numberOfWasInfected+" never infected not immunized "+numberOfNeverInfectedNotImmunized);
	}

}
