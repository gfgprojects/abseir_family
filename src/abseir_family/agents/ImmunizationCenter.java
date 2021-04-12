package abseir_family.agents;

import abseir_family.GGmodel_builder;
import abseir_family.agents.Individual;
import abseir_family.agents.Family;
import abseir_family.utils.FragilityComparator;
import abseir_family.utils.NumberOfContactsComparator;
import abseir_family.utils.NumberOfNeverInfectedComparator;
import abseir_family.utils.InfectionAgeComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.lang.Math;

public class ImmunizationCenter{
	int availableImmunizationDoses=0;
	int tmpImmunizationDosesToFragile,tmpImmunizationDosesToManyContacts,tmpNumberOfFrigileIndividualsStillToImmunize,tmpNumberOfManyContactsIndividualsStillToImmunize,tmpImmunizationsToFragile,tmpImmunizationsToManyContacts;
	Individual tmpIndividual;
	Family tmpFamily;
	public ArrayList<Individual> individualsList=new ArrayList<Individual>();
	public ArrayList<Family> familiesList;
	public ArrayList<Individual> individualsToBeImmunizedList=new ArrayList<Individual>();
	public ArrayList<Individual> individualsToBeImmunizedByFragilityList=new ArrayList<Individual>();
	public ArrayList<Individual> individualsToBeImmunizedByContactsList=new ArrayList<Individual>();
	public ArrayList<Individual> individualsToBeImmunizedThatWasInfectedList=new ArrayList<Individual>();
	public ArrayList<Family> familiesToBeImmunizedList=new ArrayList<Family>();

	public ImmunizationCenter(ArrayList<Individual> argIndividualsList,ArrayList<Family> argFamiliesList){
		individualsList=argIndividualsList;
		familiesList=argFamiliesList;
		for(Individual anIndividual : individualsList){
			if(!anIndividual.getImmunization()){
				individualsToBeImmunizedList.add(anIndividual);
			}
		}
		for(Family aFamily : familiesList){
			if(aFamily.getNumberOfNeverInfected()>0){
				familiesToBeImmunizedList.add(aFamily);
			}
		}


	if(GGmodel_builder.verboseFlag){System.out.println("IMMUNIZATION CENTRE CREATED");}
	}

	public void updateIndividualsLists(){
		individualsToBeImmunizedList=new ArrayList<Individual>();
		for(Individual anIndividual : individualsList){
			if(!anIndividual.getImmunization() && !anIndividual.getWasInfected()){
				individualsToBeImmunizedList.add(anIndividual);
			}
		}
		individualsToBeImmunizedByFragilityList=new ArrayList<Individual>(individualsToBeImmunizedList);
		Collections.sort(individualsToBeImmunizedByFragilityList,new FragilityComparator());
		Collections.reverse(individualsToBeImmunizedByFragilityList);

		individualsToBeImmunizedByContactsList=new ArrayList<Individual>(individualsToBeImmunizedList);
		Collections.sort(individualsToBeImmunizedByContactsList,new NumberOfContactsComparator());
		Collections.reverse(individualsToBeImmunizedByContactsList);
		if(GGmodel_builder.verboseFlag){System.out.println("   frigile sorted");}
		for(Individual anIndividual : individualsToBeImmunizedByFragilityList){
			if(GGmodel_builder.verboseFlag){anIndividual.printInfo();}
		}
		if(GGmodel_builder.verboseFlag){System.out.println("   many contacts sorted");};
		for(Individual anIndividual : individualsToBeImmunizedByContactsList){
			if(GGmodel_builder.verboseFlag){anIndividual.printInfo();}
		}
	}
	public void updateFamiliesLists(){
//		System.out.println("Families");
		familiesToBeImmunizedList=new ArrayList<Family>();
		for(Family aFamily : familiesList){
			aFamily.updateState();
			if(aFamily.getNumberOfNeverInfectedNotImmunized()>0){
				familiesToBeImmunizedList.add(aFamily);
			}
		}
		Collections.sort(familiesToBeImmunizedList,new NumberOfNeverInfectedComparator());
		Collections.reverse(familiesToBeImmunizedList);
		if(GGmodel_builder.verboseFlag){
			for(Family aFamily : familiesToBeImmunizedList){
				aFamily.printInfo();
			}
		}
	}
	public void increaseAvailableImmunizationDoses(){
		availableImmunizationDoses+=GGmodel_builder.newImmunizationDosesAvailableAtEachTime;
		if(availableImmunizationDoses>2*GGmodel_builder.newImmunizationDosesAvailableAtEachTime){
			availableImmunizationDoses=2*GGmodel_builder.newImmunizationDosesAvailableAtEachTime;
		}
		if(GGmodel_builder.verboseFlag){System.out.println("   available doses: "+availableImmunizationDoses);}
	}

	public void performIndividualsImmunization(){
		tmpNumberOfFrigileIndividualsStillToImmunize=individualsToBeImmunizedByFragilityList.size();
		tmpNumberOfManyContactsIndividualsStillToImmunize=individualsToBeImmunizedByContactsList.size();
		tmpImmunizationDosesToFragile=(int)Math.round(availableImmunizationDoses*GGmodel_builder.shareOfAvailableImmunizationDosesToFragile);
		tmpImmunizationDosesToManyContacts=availableImmunizationDoses-tmpImmunizationDosesToFragile;
		if(GGmodel_builder.verboseFlag){System.out.println("Going to immunize fragile: "+tmpImmunizationDosesToFragile+" many contacts: "+tmpImmunizationDosesToManyContacts+" available vax: "+availableImmunizationDoses);}

//immunize people that was not infected
 if(GGmodel_builder.verboseFlag){System.out.println("immunizing fragile");}
		if(tmpImmunizationDosesToFragile>0){
			if(tmpNumberOfFrigileIndividualsStillToImmunize>0){
				if(tmpImmunizationDosesToFragile>tmpNumberOfFrigileIndividualsStillToImmunize){
					tmpImmunizationsToFragile=tmpNumberOfFrigileIndividualsStillToImmunize;
				}
				else{
					tmpImmunizationsToFragile=tmpImmunizationDosesToFragile;
				}
				for(int i=0;i<tmpImmunizationsToFragile;i++){
					tmpIndividual=individualsToBeImmunizedByFragilityList.get(i);
					tmpIndividual.setImmunization();
 					if(GGmodel_builder.verboseFlag){tmpIndividual.printInfo();}
					availableImmunizationDoses--;
				}
			}

		}

 if(GGmodel_builder.verboseFlag){System.out.println("immunizing many contacts");}
		if(tmpImmunizationDosesToManyContacts>0){
			int runner=0;
			while(runner < tmpNumberOfManyContactsIndividualsStillToImmunize && availableImmunizationDoses>0){
				tmpIndividual=individualsToBeImmunizedByContactsList.get(runner);
				if(!tmpIndividual.getImmunization()){
					tmpIndividual.setImmunization();
 					if(GGmodel_builder.verboseFlag){tmpIndividual.printInfo();}
					availableImmunizationDoses--;
				}
				runner++;
			}
		}
//Immunize people that was infected if additional doses exists
		/*
		if(availableImmunizationDoses>0){
			individualsToBeImmunizedThatWasInfectedList=new ArrayList<Individual>();
			for(Individual anIndividual : individualsList){
				if(!anIndividual.getImmunization() && anIndividual.getInfectionAge()<1){
					individualsToBeImmunizedThatWasInfectedList.add(anIndividual);
				}
			}
			Collections.sort(individualsToBeImmunizedThatWasInfectedList,new InfectionAgeComparator());
			Collections.reverse(individualsToBeImmunizedThatWasInfectedList);
			int runner=0;
			while(runner < individualsToBeImmunizedThatWasInfectedList.size() && availableImmunizationDoses>0){
				tmpIndividual=individualsToBeImmunizedThatWasInfectedList.get(runner);
				tmpIndividual.setImmunization();
				availableImmunizationDoses--;
				runner++;
			}

		}
		*/
	}
		public void performFamiliesImmunization(){
			int frunner=0;
			while(frunner < familiesToBeImmunizedList.size() && availableImmunizationDoses>0){
				tmpFamily=familiesToBeImmunizedList.get(frunner);
				availableImmunizationDoses-=tmpFamily.getNumberOfNeverInfectedNotImmunized();
				tmpFamily.performMembersImmunization();
				frunner++;
			}
			if(GGmodel_builder.verboseFlag){System.out.println("immunized families: "+frunner+" avail doses: "+availableImmunizationDoses);}
//Immunize people that was infected if additional doses exists
/*
		if(availableImmunizationDoses>0){
			individualsToBeImmunizedThatWasInfectedList=new ArrayList<Individual>();
			for(Individual anIndividual : individualsList){
				if(!anIndividual.getImmunization() && anIndividual.getInfectionAge()<1){
					individualsToBeImmunizedThatWasInfectedList.add(anIndividual);
				}
			}
			Collections.sort(individualsToBeImmunizedThatWasInfectedList,new InfectionAgeComparator());
			Collections.reverse(individualsToBeImmunizedThatWasInfectedList);
			int runner=0;
			while(runner < individualsToBeImmunizedThatWasInfectedList.size() && availableImmunizationDoses>0){
				tmpIndividual=individualsToBeImmunizedThatWasInfectedList.get(runner);
				tmpIndividual.setImmunization();
				availableImmunizationDoses--;
				runner++;
			}

		}
*/
		}

}
