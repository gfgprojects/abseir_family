package abseir_family.utils;

import abseir_family.agents.Individual;

import java.util.Comparator;

public class FragilityComparator implements Comparator<Individual> {

public int compare(Individual individual1,Individual individual2){
	return Double.compare(individual1.getFragility(),individual2.getFragility());
}
}
