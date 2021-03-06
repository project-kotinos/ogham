package fr.sii.ogham.testing.sms.simulator.jsmpp;

import static fr.sii.ogham.testing.extension.junit.SmppServerRule.DEFAULT_PORT;

import java.util.List;

import org.jsmpp.bean.SubmitSm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import fr.sii.ogham.testing.sms.simulator.SmppServerException;
import fr.sii.ogham.testing.sms.simulator.SmppServerSimulator;
import fr.sii.ogham.testing.sms.simulator.config.SimulatorConfiguration;

/**
 * The server simulator based on <a href="http://jsmpp.org/">jsmpp</a> samples.
 */
public class JSMPPServer implements SmppServerSimulator<SubmitSm> {
	private static final Logger LOG = LoggerFactory.getLogger(JSMPPServer.class);

	private Thread thread;
	private final JSMPPServerSimulator simulator;

	/**
	 * Initializes with default port ({@link SmppServerRule#DEFAULT_PORT}) and
	 * provided configuration.
	 * 
	 * @param config
	 *            the server configuration
	 */
	public JSMPPServer(SimulatorConfiguration config) {
		this(DEFAULT_PORT, config);
	}

	/**
	 * Initializes with provided port and provided configuration.
	 * 
	 * @param port
	 *            the port to use for server
	 * @param config
	 *            the server configuration
	 */
	public JSMPPServer(int port, SimulatorConfiguration config) {
		super();
		simulator = new JSMPPServerSimulator(port, config);
	}

	@Override
	public synchronized void start() throws SmppServerException {
		try {
			LOG.debug("starting simulator thread...");
			simulator.reset();
			thread = new Thread(simulator);
			thread.start();
			simulator.waitTillRunning(5000L);
			LOG.debug("simulator started");
		} catch (ServerStartupException e) {
			throw new SmppServerException("Failed to start JSMPP server", e);
		}
	}

	@Override
	public synchronized void stop() throws SmppServerException {
		try {
			LOG.debug("stopping simulator thread...");
			simulator.stop();
			thread.interrupt();
			thread.join();
			LOG.debug("simulator stopped");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new SmppServerException("Failed to stop JSMPP server", e);
		}
	}

	@Override
	public int getPort() {
		return simulator.getPort();
	}

	@Override
	public List<SubmitSm> getReceivedMessages() {
		return simulator.getReceivedMessages();
	}

}
