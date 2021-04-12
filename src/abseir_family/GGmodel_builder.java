package abseir_family;
import abseir_family.agents.Individual;
import abseir_family.agents.Family;
import abseir_family.dynamics.GGmodel_scheduler;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import java.util.ArrayList;
import repast.simphony.random.RandomHelper;
import cern.jet.random.Poisson;
public class GGmodel_builder implements ContextBuilder<Object>{
	public static boolean verboseFlag=false;
	public static int caseZetoTime=10;
	public static int swabTestAndQuarantineStartingTime=2000; //15
	public static int immunizationStartingTime=1; //100
	public static boolean individualsBasedInnumization=false;
	public static int newImmunizationDosesAvailableAtEachTime=150;
	public static double shareOfAvailableImmunizationDosesToFragile=1.0;
	public static int recoveryTimeInAsymptomatics=8;
	public static int minimumDiscoveryTimeInInfected=3; //latency
	public static int latencyTime=3; //latency
	public static int quarantineLength=15;
	public static int immunityAfterFirstInfection=-90;
	public static int immunityAfterImmunization=-1000;
	public static double contagionProbabilityInFamily=0.1;
	public static double contagionProbabilityOutOfFamily=0.01;
	public static int minNumberOfContactsOutOfFamily=10;
	public static int maxNumberOfContactsOutOfFamily=10;
	public static int caseZeroNumberOfContactsOutOfFamily=10;
	double averageFamilySize=2.5; //2.58
	boolean givenFamiliesSizeDistributionFlag = true;
	public static int numberOfSusceptibleInCaseZeroFamily=3;
	int batchStoppingTime=2;
	int nOfIndividuals=20000;
	int nOfIndividualsCreated=0;
	int nOfFamiliesCreated=0;
//	int minFamilySize=1;
//	int maxFamilySize=20;
	int tmpFamilySize;
	double shareOfAsymptomatic=0.0;
	public static int nOfAsymptomatic;
	public static double oneAsymptomaticEach;
	Individual tmpIndividual;
	Family tmpFamily;
	public static ArrayList<Individual> allIndividualsList=new ArrayList<Individual>();
	ArrayList<Family> allFamiliesList=new ArrayList<Family>();
	public static ISchedule schedule;
	Poisson familiesSizeDistribution;

	public Context<Object> build(Context<Object> context) {
		batchStoppingTime=0;
		if(verboseFlag){
			System.out.println("Start initialization");
		}
		//RandomHelper.setSeed(1535229879);	
		//RandomHelper.setSeed(1604413942);	
		//RandomHelper.setSeed(1929557944);
		//System.out.println(RandomHelper.getSeed());
		if(givenFamiliesSizeDistributionFlag){
			ArrayList<Integer> numberOfFamiliesForEachSizeList=new ArrayList<Integer>();
			//average family size 2.5
			/*
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(469));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(1173));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(1466));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(1222));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(764));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(382));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(159));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(57));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(18));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(5));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(1));
			*/

			//average family size 3.5
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(134));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(470));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(823));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(960));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(840));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(588));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(343));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(171));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(75));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(29));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(10));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(3));
			numberOfFamiliesForEachSizeList.add(Integer.valueOf(1));

			for(int i=0;i<numberOfFamiliesForEachSizeList.size();i++){
				for(int k=0;k<(int)numberOfFamiliesForEachSizeList.get(i);k++){
					tmpFamily=new Family(nOfFamiliesCreated);
					allFamiliesList.add(tmpFamily);
					for(int j=0;j<i+1;j++){
						tmpIndividual=new Individual(nOfIndividualsCreated,j,nOfFamiliesCreated);
						allIndividualsList.add(tmpIndividual);
						nOfIndividualsCreated++;
						tmpFamily.addMember(tmpIndividual);
						context.add(tmpIndividual);
					}
					tmpFamily.setIndividualsFamily();
					context.add(tmpFamily);
					nOfFamiliesCreated++;
				}
			}
		} 
		else{
			familiesSizeDistribution=RandomHelper.createPoisson(averageFamilySize);
			//familiesSizeDistribution=RandomHelper.createBeta(2,2);
			/*
			   for(int i=0;i<1000;i++){
			//System.out.println((int)(familiesSizeDistribution.nextDouble()*(maxFamilySize-minFamilySize+1))+minFamilySize);
			System.out.println(familiesSizeDistribution.nextInt()+1);
			   }
			//Families an Individuals creation
			for(int i=0;i<10;i++){
			tmpFamily=new Family(nOfFamiliesCreated);
			allFamiliesList.add(tmpFamily);
			tmpFamilySize=5;
			for(int j=0;j<tmpFamilySize;j++){
			tmpIndividual=new Individual(nOfIndividualsCreated,j,nOfFamiliesCreated);
			allIndividualsList.add(tmpIndividual);
			nOfIndividualsCreated++;
			tmpFamily.addMember(tmpIndividual);
			context.add(tmpIndividual);
			}
			tmpFamily.setIndividualsFamily();
			context.add(tmpFamily);
			nOfFamiliesCreated++;
			}


			for(int i=0;i<40;i++){
			tmpFamily=new Family(nOfFamiliesCreated);
			allFamiliesList.add(tmpFamily);
			tmpFamilySize=1;
			for(int j=0;j<tmpFamilySize;j++){
			tmpIndividual=new Individual(nOfIndividualsCreated,j,nOfFamiliesCreated);
			allIndividualsList.add(tmpIndividual);
			nOfIndividualsCreated++;
			tmpFamily.addMember(tmpIndividual);
			context.add(tmpIndividual);
			}
			tmpFamily.setIndividualsFamily();
			context.add(tmpFamily);
			nOfFamiliesCreated++;
			}

*/		

			while(nOfIndividualsCreated<nOfIndividuals){
				tmpFamily=new Family(nOfFamiliesCreated);
				allFamiliesList.add(tmpFamily);
				//			tmpFamilySize=RandomHelper.nextIntFromTo(minFamilySize,maxFamilySize);
				tmpFamilySize=familiesSizeDistribution.nextInt()+1;
				//			System.out.println(tmpFamilySize);
				if((nOfIndividuals-nOfIndividualsCreated)<tmpFamilySize){
					tmpFamilySize=nOfIndividuals-nOfIndividualsCreated;
				}
				for(int j=0;j<tmpFamilySize;j++){
					tmpIndividual=new Individual(nOfIndividualsCreated,j,nOfFamiliesCreated);
					allIndividualsList.add(tmpIndividual);
					nOfIndividualsCreated++;
					tmpFamily.addMember(tmpIndividual);
					context.add(tmpIndividual);
				}
				tmpFamily.setIndividualsFamily();
				context.add(tmpFamily);
				nOfFamiliesCreated++;
			}
		}
//		System.out.println("nf "+nOfFamiliesCreated+" ni "+nOfIndividualsCreated);
		//Setting up asymptomatic people
		if(verboseFlag){System.out.println("SET ASYMPTOMATICS");}
		nOfAsymptomatic=(int)(shareOfAsymptomatic*allIndividualsList.size());
		oneAsymptomaticEach=(double)allIndividualsList.size()/nOfAsymptomatic;
		//Stop setting up asymptomatics

		/*
		   int case0ID=RandomHelper.nextIntFromTo(0,nOfIndividualsCreated-1);
		   System.out.println("primo "+case0);
		   Individual case0=allIndividualsList.get(case0ID);
		   case0.setAsymptomatic();
		   case0.setInfection(true);
		   */
		//System.out.println(RandomHelper.getSeed());

		/*
		   immunizationCenter.updateIndividualsLists();
		   System.out.println();
		   immunizationCenter.increaseAvailableImmunizationDoses();
		   System.out.println();
		   immunizationCenter.performIndividualsImmunization();
		   System.out.println();
		   immunizationCenter.updateIndividualsLists();
		   System.out.println();
		   immunizationCenter.increaseAvailableImmunizationDoses();
		   System.out.println();
		   immunizationCenter.performIndividualsImmunization();
		   System.out.println();


		   for(Individual anIndividual : allIndividualsList){
		   anIndividual.printInfo();
		   }
		   */





		schedule = RunEnvironment.getInstance().getCurrentSchedule();
		GGmodel_scheduler ggmodel_scheduler=new GGmodel_scheduler(context,allIndividualsList,allFamiliesList);
		ggmodel_scheduler.scheduleEvents();



		if (RunEnvironment.getInstance().isBatch())
		{
			RunEnvironment.getInstance().endAt(batchStoppingTime);
		}


		return context;
	}

}
