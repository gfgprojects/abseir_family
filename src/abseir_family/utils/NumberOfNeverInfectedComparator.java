package abseir_family.utils;

import abseir_family.agents.Family;

import java.util.Comparator;

public class NumberOfNeverInfectedComparator implements Comparator<Family> {

public int compare(Family family1,Family family2){
	return Integer.compare(family1.getNumberOfNeverInfectedNotImmunized(),family2.getNumberOfNeverInfectedNotImmunized());
}
}
