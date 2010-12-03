package pt.ist.fenixWebFramework.renderers.validators;

import pt.ist.fenixWebFramework.renderers.components.HtmlCheckBoxList;
import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class RequiredNrItemsValidator extends HtmlValidator {
	private Integer nrRequiredItems;

	public RequiredNrItemsValidator() {
		super();
		setMessage("renderers.validator.invalid.nrItems");
	}

	public RequiredNrItemsValidator(HtmlChainValidator htmlChainValidator) {
		super(htmlChainValidator);

		setMessage("renderers.validator.invalid.nrItems");
	}

	@Override
	public void performValidation() {
		HtmlCheckBoxList component = (HtmlCheckBoxList) getComponent();

		String values[] = component.getValues();
		if (getNrRequiredItems() == null) {
			throw new RuntimeException(
					"renderers.validator.nr.items.not.specified");
		}

		setValid(values.length >= getNrRequiredItems().intValue());

	}

	public void setNrRequiredItems(Integer nrRequiredItems) {
		this.nrRequiredItems = nrRequiredItems;
	}

	public Integer getNrRequiredItems() {
		return nrRequiredItems;
	}

}
