package fr.sii.ogham.testing.helper.sms.util;

import fr.sii.ogham.testing.helper.sms.bean.SubmitSm;

/**
 * Some utility functions for SMS.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public final class SmsUtils {

	/**
	 * Get the text content of the SMS.
	 * 
	 * @param actual
	 *            the SMS
	 * @return the SMS content
	 */
	public static String getSmsContent(SubmitSm actual) {
		return MessageDecoder.decode(actual);
	}

	private SmsUtils() {
		super();
	}
}