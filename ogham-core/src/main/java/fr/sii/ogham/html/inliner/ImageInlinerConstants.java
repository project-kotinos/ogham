package fr.sii.ogham.html.inliner;

/**
 * Common constants for image inlining.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public final class ImageInlinerConstants {
	/**
	 * <p>
	 * The attribute name to indicate which strategy for inlining to apply if
	 * the default one is not appropriated.
	 * </p>
	 * 
	 * For example:
	 * 
	 * <pre>
	 * {@code
	 * 		<img src="..." data-inline-image="base64" />
	 * }
	 * </pre>
	 * 
	 * It forces to use <code>base64</code> inlining for this image.
	 * 
	 * 
	 * <p>
	 * You can also skip inlining by using "skip" value:
	 * 
	 * <pre>
	 * {@code
	 * 		<img src="..." data-inline-image="skip" />
	 * }
	 * </pre>
	 * 
	 * @see InlineModes
	 */
	public static final String INLINE_MODE_ATTR = "data-inline-image";

	/**
	 * Attribute to mark an image as already inlined in order to not process it
	 * again
	 */
	public static final String INLINED_ATTR = "data-image-inlined";

	/**
	 * Interface for defining an inline mode
	 * 
	 * @author Aurélien Baudet
	 *
	 */
	public interface InlineMode {
		/**
		 * The inline mode value
		 * 
		 * @return inline mode value
		 */
		String mode();

		/**
		 * Indicates if the mode is the same as the current mode.
		 * 
		 * @param mode
		 *            the mode to check if same
		 * @return true if same value, false otherwise
		 */
		boolean is(String mode);
	}

	/**
	 * Provide predefined inline modes
	 * 
	 * @author Aurélien Baudet
	 *
	 */
	public enum InlineModes implements InlineMode {
		/**
		 * Attach the image to the email and references it in the HTML using a
		 * Content-ID (CID).
		 */
		ATTACH("attach"),
		/**
		 * Encode the image content to a base64 string that is directly used by
		 * image {@code src} attribute
		 */
		BASE64("base64"),
		/**
		 * Do not inline the image
		 */
		SKIP("skip");

		private final String mode;

		InlineModes(String mode) {
			this.mode = mode;
		}

		@Override
		public String mode() {
			return mode;
		}

		@Override
		public boolean is(String mode) {
			return this.mode.equals(mode);
		}
	}

	private ImageInlinerConstants() {
		super();
	}
}
