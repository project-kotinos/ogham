package fr.sii.ogham.sms;

import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM;
import static com.cloudhopper.smpp.SmppBindType.TRANSCEIVER;
import static fr.sii.ogham.sms.builder.cloudhopper.InterfaceVersion.VERSION_3_4;

import com.cloudhopper.smpp.SmppBindType;

import fr.sii.ogham.sms.builder.cloudhopper.InterfaceVersion;

public final class CloudhopperConstants {
	/**
	 * The configurer has a priority of 40000 in order to be applied after
	 * templating configurers.
	 */
	public static final int DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY = 40000;
	/**
	 * The implementation has a priority of 40000.
	 */
	public static final int DEFAULT_CLOUDHOPPER_IMPLEMENTATION_PRIORITY = 40000;

	/**
	 * Enable automatic guessing of encoding by default.
	 * 
	 */
	public static final boolean DEFAULT_AUTO_GUESS_ENABLED = true;
	/**
	 * The priority for UCS-2 encoding. This is used by automatic guessing.
	 * Disabled by default (0).
	 */
	public static final int DEFAULT_GSM7BIT_PACKED_ENCODING_PRIORITY = 0;
	/**
	 * The priority for UCS-2 encoding. This is used by automatic guessing.
	 */
	public static final int DEFAULT_UCS2_ENCODING_PRIORITY = 90000;
	/**
	 * The priority for Latin-1 encoding. This is used by automatic guessing.
	 */
	public static final int DEFAULT_LATIN1_ENCODING_PRIORITY = 98000;
	/**
	 * The priority for GSM8 encoding. This is used by automatic guessing.
	 */
	public static final int DEFAULT_GSM8_ENCODING_PRIORITY = 99000;

	/**
	 * Enable use of short_message field to carry text message (named User Data)
	 * by default
	 */
	public static final boolean DEFAULT_USE_SHORT_MESSAGE = true;
	/**
	 * Disable use of message_payload optional TLV (Tag-Value-Length) parameter
	 * to carry text message (named User Data) by default
	 */
	public static final boolean DEFAULT_USE_TLV_MESSAGE_PAYLOAD = false;
	/**
	 * Enable automatic mode based on SMPP interface version by default
	 */
	public static final boolean DEFAULT_AUTO_DATA_CODING_SCHEME_ENABLED = true;
	/**
	 * Enable message splitting by default
	 */
	public static final boolean DEFAULT_SPLIT_ENABLED = true;

	/**
	 * The default port for SMPP protocol
	 */
	public static final int DEFAULT_SMPP_PORT = 2775;

	/**
	 * The default bind command type (TRANSCEIVER)
	 */
	public static final SmppBindType DEFAULT_BIND_TYPE = TRANSCEIVER;

	/**
	 * The default SMPP protocol version (3.4)
	 */
	public static final InterfaceVersion DEFAULT_INTERFACE_VERSION = VERSION_3_4;

	/**
	 * Default Cloudhopper Charset that should be used if nothing else is
	 * configured.
	 */
	public static final String DEFAULT_CHARSET = NAME_GSM;
	/**
	 * Default maximum amount of time (in milliseconds) to wait for the success
	 * of a bind attempt to the SMSC (5000 ms).
	 */
	public static final long DEFAULT_BIND_TIMEOUT = 5000L;
	/**
	 * Default maximum amount of time (in milliseconds) to wait for a
	 * establishing the connection (10000 ms).
	 */
	public static final long DEFAULT_CONNECT_TIMEOUT = 10000L;
	/**
	 * Default amount of time (milliseconds) to wait for an endpoint to respond
	 * to a request before it expires (disabled [-1]).
	 */
	public static final long DEFAULT_REQUEST_EXPIRY_TIMEOUT = -1L;
	/**
	 * Default amount of time (milliseconds) between executions of monitoring
	 * the window for requests that expire. It's recommended that this generally
	 * either matches or is half the value of requestExpiryTimeout. Therefore,
	 * at worst a request would could take up 1.5X the requestExpiryTimeout to
	 * clear out (disabled [-1]).
	 */
	public static final long DEFAULT_WINDOW_MONITOR_INTERVAL = -1L;
	/**
	 * Default maximum number of requests permitted to be outstanding
	 * (unacknowledged) at a given time. Must be &gt; 0. Defaults to 1.
	 */
	public static final int DEFAULT_WINDOW_SIZE = 1;
	/**
	 * Default amount of time (milliseconds) to wait until a slot opens up in
	 * the sendWindow (60000 ms).
	 */
	public static final long DEFAULT_WINDOW_WAIT_TIMEOUT = 60000L;
	/**
	 * Default maximum amount of time (in milliseconds) to wait for bytes to be
	 * written when creating a new SMPP session (no timeout [0], for backwards
	 * compatibility).
	 */
	public static final long DEFAULT_WRITE_TIMEOUT = 0L;
	/**
	 * Set the maximum amount of time (in milliseconds) to wait until a valid
	 * response is received when a "submit" request is synchronously sends to
	 * the remote endpoint. The timeout value includes both waiting for a
	 * "window" slot, the time it takes to transmit the actual bytes on the
	 * socket, and for the remote endpoint to send a response back (5000 ms).
	 */
	public static final long DEFAULT_RESPONSE_TIMEOUT = 5000L;
	/**
	 * Set the maximum amount of time (in milliseconds) to wait until the
	 * session is unbounded, waiting up to a specified period of milliseconds
	 * for an unbind response from the remote endpoint. Regardless of whether a
	 * proper unbind response was received, the socket/channel is closed (5000
	 * ms).
	 */
	public static final long DEFAULT_UNBIND_TIMEOUT = 5000L;
	/**
	 * Default maximum number of attempts for connecting to SMSC (10).
	 */
	public static final int DEFAULT_CONNECT_MAX_RETRIES = 10;
	/**
	 * Default delay (in milliseconds) between connection attempts (5000 ms).
	 */
	public static final long DEFAULT_CONNECT_RETRY_DELAY = 5000L;

	/**
	 * Disable sending EnquireLink request to keep session alive by default.
	 */
	public static final boolean DEFAULT_KEEP_ALIVE_ENABLED = false;

	/**
	 * Disable sending EnquireLink request at startup (meaning that the
	 * EnquireLink requests will start after the first message is sent).
	 */
	public static final boolean DEFAULT_KEEP_ALIVE_CONNECT_AT_STARTUP = false;

	/**
	 * Default fixed delay (in milliseconds) between two EnquireLink messages
	 * (30000 ms).
	 */
	public static final long DEFAULT_ENQUIRE_LINK_INTERVAL = 30000L;
	/**
	 * Default maximum amount of time (in milliseconds) to wait for receiving a
	 * response from the server to an EnquireLink request (10000 ms).
	 */
	public static final long DEFAULT_ENQUIRE_LINK_RESPONSE_TIMEOUT = 10000L;
	/**
	 * Disable reusing same session for sending messages (use a new session for
	 * every message).
	 */
	public static final boolean DEFAULT_REUSE_SESSION_ENABLED = false;
	/**
	 * Default expiration delay (30000 ms).
	 * 
	 * To check if the session is still alive, an EnquireLink request is sent.
	 * The request is sent just before sending the message. This is the time (in
	 * milliseconds) to wait before considering last EnquireLink response as
	 * expired (need to send a new EnquireLink request to check if session is
	 * still alive).
	 * 
	 * This is needed to prevent sending EnquireLink request every time a
	 * message has to be sent. Instead it considers that the time elapsed
	 * between now and the last EnquireLink response (or the last sent message)
	 * is not enough so a new EnquireLink is not necessary to check if session
	 * is still alive.
	 * 
	 * Set to 0 to always check session before sending message.
	 */
	public static final long DEFAULT_LAST_INTERACTION_EXPIRATION_DELAY = 30000L;
	/**
	 * Default response timeout to an EnquireLink request (10000 ms).
	 * 
	 * To check if the session is still alive, an EnquireLink request is sent.
	 * This request may fail since the session may be killed by the server. The
	 * timeout ensures that the client doesn't wait too long for a response that
	 * may never come. The maximum amount of time (in milliseconds) to wait for
	 * receiving a response from the server to an EnquireLink request.
	 */
	public static final long DEFAULT_ENQUIRE_LINK_REUSE_RESPONSE_TIMEOUT = 10000L;
	/**
	 * Default number of consecutive EnquireLink requests that end in a timeout.
	 */
	public static final Integer DEFAULT_KEEP_ALIVE_MAX_CONSECUTIVE_TIMEOUTS = 3;

	private CloudhopperConstants() {
		super();
	}
}
