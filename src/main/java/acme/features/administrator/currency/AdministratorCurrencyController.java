
package acme.features.administrator.currency;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractController;
import acme.client.data.accounts.Administrator;
import acme.entities.currency.Currency;

@Controller
public class AdministratorCurrencyController extends AbstractController<Administrator, Currency> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministradorCurrencyListService listService;


	// Constructors -----------------------------------------------------------
	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
	}

}
