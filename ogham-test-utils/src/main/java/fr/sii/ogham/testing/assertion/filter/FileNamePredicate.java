package fr.sii.ogham.testing.assertion.filter;

import javax.mail.MessagingException;
import javax.mail.Part;

import com.google.common.base.Predicate;

/**
 * Predicate that matches the {@link Part} only if {@link Part#getFileName()}
 * exactly matches the provided filename.
 * 
 * @author Aurélien Baudet
 *
 */
public class FileNamePredicate implements Predicate<Part> {
	private final String filename;

	public FileNamePredicate(String filename) {
		super();
		this.filename = filename;
	}

	@Override
	public boolean apply(Part input) {
		try {
			return filename.equals(input.getFileName());
		} catch (MessagingException e) {
			throw new AssertionError("Failed to access message", e);
		}
	}

	@Override
	public String toString() {
		return "named '" + filename + "'";
	}
}