
package acme.forms;

import java.util.Map;

import acme.client.data.AbstractForm;
import acme.features.manager.dashboard.MoneyStatistics;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerDashboard extends AbstractForm {

	// Serialisation identifier -----------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	int								totalNumberOfMust;
	int								totalNumberOfShould;
	int								totalNumberOfCould;
	int								totalNumberOfWont;
	Double							averageEstimatedCost;
	Double							deviationEstimatedCost;
	Double							minimumEstimatedCost;
	Double							maximumEstimatedCost;
	Double							averageCost;
	Double							deviationCost;
	Double							minimumCost;
	Double							maximumCost;

	Map<String, MoneyStatistics>	moneyStatistics;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
