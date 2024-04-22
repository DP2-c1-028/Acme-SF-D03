
package acme.features.developer.trainingModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.training_modules.TrainingModule;
import acme.entities.training_modules.TrainingModuleDifficulty;
import acme.roles.Developer;

@Service
public class DeveloperTrainingModulePublishService extends AbstractService<Developer, TrainingModule> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private DeveloperTrainingModuleRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		int developerId;
		TrainingModule trainingModule;

		id = super.getRequest().getData("id", int.class);
		trainingModule = this.repository.findOneTrainingModuleById(id);

		developerId = super.getRequest().getPrincipal().getActiveRoleId();

		status = developerId == trainingModule.getDeveloper().getId() && !trainingModule.isPublished();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrainingModule object;
		Integer id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneTrainingModuleById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final TrainingModule object) {
		assert object != null;

		Integer developerId = super.getRequest().getPrincipal().getActiveRoleId();
		Developer developer = this.repository.findOneDeveloperById(developerId);
		object.setDeveloper(developer);
		super.bind(object, "code", "creationMoment", "updateMoment", "difficulty", "details", "totalTime", "link", "project");
	}

	@Override
	public void validate(final TrainingModule object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			TrainingModule trainingModuleSameCode = this.repository.findOneTrainingModuleByCode(object.getCode());

			if (trainingModuleSameCode != null)
				super.state(trainingModuleSameCode.getId() == object.getId(), "code", "developer.training-module.form.error.code");
		}

		if (!super.getBuffer().getErrors().hasErrors("unpublishable")) {

			boolean publishable;

			publishable = !this.repository.findTrainingSessionsByTrainingModuleId(object.getId()).isEmpty();

			super.state(publishable, "*", "developer.training-module.form.error.unpublishable");
		}

	}

	@Override
	public void perform(final TrainingModule object) {
		assert object != null;

		object.setPublished(true);

		this.repository.save(object);
	}

	@Override
	public void unbind(final TrainingModule object) {
		assert object != null;

		SelectChoices difficultyChoices;
		SelectChoices projectChoices;

		difficultyChoices = SelectChoices.from(TrainingModuleDifficulty.class, object.getDifficulty());
		projectChoices = SelectChoices.from(this.repository.findAllProjects(), "title", object.getProject());

		Dataset dataset;

		dataset = super.unbind(object, "code", "creationMoment", "updateMoment", "difficulty", "details", "totalTime", "link", "published", "project");
		dataset.put("difficulties", difficultyChoices);
		dataset.put("projects", projectChoices);

		super.getResponse().addData(dataset);
	}

}