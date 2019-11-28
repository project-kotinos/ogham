package fr.sii.ogham.assertion.sms;

import java.util.regex.Pattern;

import fr.sii.ogham.assertion.context.Context;
import fr.sii.ogham.helper.sms.bean.SubmitSm;

/**
 * Dedicated context for one particular {@link SubmitSm}
 * 
 * @author Aurélien Baudet
 *
 * @param <S>
 *            the type of {@link SubmitSm}
 */
public class PduRequestWithContext<S extends SubmitSm> implements Context {
	private final S request;
	private final String name;
	private final Context parent;

	/**
	 * @param request
	 *            the received request
	 * @param name
	 *            a name used in context evaluation
	 * @param parent
	 *            the parent context
	 */
	public PduRequestWithContext(S request, String name, Context parent) {
		super();
		this.request = request;
		this.name = name;
		this.parent = parent;
	}

	@Override
	public String evaluate(String template) {
		String result = template.replaceAll(Pattern.quote("${name}"), name);
		return parent.evaluate(result);
	}

	/**
	 * @return the received request
	 */
	public S getRequest() {
		return request;
	}

}
