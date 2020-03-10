package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.AbstractAutofillDefaultValueBuilder;
import fr.sii.ogham.core.builder.BuildContext;

public class AutofillDefaultEmailAddressBuilder<V> extends AbstractAutofillDefaultValueBuilder<AutofillDefaultEmailAddressBuilder<V>, AutofillEmailBuilder, V> {

	public AutofillDefaultEmailAddressBuilder(AutofillEmailBuilder parent, Class<V> valueClass, BuildContext buildContext) {
		super(AutofillDefaultEmailAddressBuilder.class, parent, valueClass, buildContext);
	}

}
