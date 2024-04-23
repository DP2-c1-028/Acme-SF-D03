
package acme.features.auditor.auditRecord;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.auditRecords.AuditRecord;
import acme.entities.auditRecords.Mark;
import acme.entities.codeAudits.CodeAudit;
import acme.roles.Auditor;

@Service
public class AuditorAuditRecordCreateService extends AbstractService<Auditor, AuditRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuditorAuditRecordRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int codeAuditId;
		int auditorId;
		CodeAudit codeAudit;

		codeAuditId = super.getRequest().getData("codeAuditId", int.class);
		codeAudit = this.repository.findOneCodeAuditById(codeAuditId);

		auditorId = super.getRequest().getPrincipal().getActiveRoleId();

		status = auditorId == codeAudit.getAuditor().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		AuditRecord object;
		Integer codeAuditId;
		CodeAudit codeAudit;

		object = new AuditRecord();
		Integer auditorId = super.getRequest().getPrincipal().getActiveRoleId();
		Auditor auditor = this.repository.findOneAuditorById(auditorId);

		codeAuditId = super.getRequest().getData("codeAuditId", int.class);
		codeAudit = this.repository.findOneCodeAuditById(codeAuditId);

		object.setAuditor(auditor);
		object.setCodeAudit(codeAudit);
		object.setDraftMode(true);

		super.getBuffer().addData(object);

	}

	@Override
	public void bind(final AuditRecord object) {
		assert object != null;

		super.bind(object, "code", "auditStartTime", "auditEndTime", "mark", "link");
	}

	@Override
	public void validate(final AuditRecord object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			AuditRecord auditRecordSameCode = this.repository.findOneAuditRecordByCode(object.getCode());

			if (auditRecordSameCode != null)
				super.state(auditRecordSameCode.getId() == object.getId(), "code", "auditor.audit-record.form.error.code");
		}

		if (!super.getBuffer().getErrors().hasErrors("auditEndTime")) {
			Date auditStartTime;
			Date auditEndTime;

			auditStartTime = object.getAuditStartTime();
			auditEndTime = object.getAuditEndTime();

			super.state(MomentHelper.isLongEnough(auditStartTime, auditEndTime, 1, ChronoUnit.HOURS) && auditEndTime.after(auditStartTime), "auditEndTime", "auditor.audit-record.form.error.audit-end-time");
		}

		if (!super.getBuffer().getErrors().hasErrors("publishedCodeAudit")) {
			Integer codeAuditId;
			CodeAudit codeAudit;

			codeAuditId = super.getRequest().getData("codeAuditId", int.class);
			codeAudit = this.repository.findOneCodeAuditById(codeAuditId);

			super.state(codeAudit.isDraftMode(), "*", "auditor.audit-record.form.error.published-code-audit");
		}
	}

	@Override
	public void perform(final AuditRecord object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final AuditRecord object) {
		assert object != null;

		Dataset dataset;
		SelectChoices choices;

		choices = SelectChoices.from(Mark.class, object.getMark());

		dataset = super.unbind(object, "code", "auditStartTime", "auditEndTime", "mark", "link", "draftMode");
		dataset.put("marks", choices);
		dataset.put("codeAuditId", super.getRequest().getData("codeAuditId", int.class));

		super.getResponse().addData(dataset);
	}

}