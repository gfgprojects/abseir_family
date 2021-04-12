package abseir_family.utils;

import abseir_family.agents.Individual;

import java.util.Comparator;

public class InfectionAgeComparator implements Comparator<Individual> {

public int compare(Individual individual1,Individual individual2){
	return Integer.compare(individual1.getInfectionAge(),individual2.getInfectionAge());
}
}
