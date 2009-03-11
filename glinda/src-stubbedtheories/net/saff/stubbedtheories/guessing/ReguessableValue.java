package net.saff.stubbedtheories.guessing;

import java.util.List;

import org.junit.experimental.theories.PotentialAssignment;
import org.junit.internal.AssumptionViolatedException;

public abstract class ReguessableValue extends PotentialAssignment {

	public ReguessableValue() {
		super();
	}

	public abstract List<ReguessableValue> reguesses(
			AssumptionViolatedException e);
}