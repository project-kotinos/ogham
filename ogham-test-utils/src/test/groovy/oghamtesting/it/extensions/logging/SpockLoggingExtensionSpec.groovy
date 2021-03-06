package oghamtesting.it.extensions.logging

import static org.assertj.core.api.Assertions.assertThat
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod

import java.util.stream.Collectors
import java.util.stream.Stream

import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Unroll
import testutils.TestPrinterFactoryAdapter

class SpockLoggingExtensionSpec extends Specification {
	def SUCCESS_HEADER = 
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" + 
			"║success                                                                                           ║\n" + 
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝";
	def SUCCESS_FOOTER =
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" + 
			"│success                                                                                           │\n" + 
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" + 
			"│SUCCESS                                                                                           │\n" + 
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘";
	
	
	def FAILURE_HEADER = 
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" + 
			"║failure                                                                                           ║\n" + 
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝";
	def FAILURE_FOOTER =
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" + 
			"│failure                                                                                           │\n" + 
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" + 
			"│FAILED                                                                                            │\n" + 
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" + 
			"│oghamtesting.it.extensions.logging.SpockLoggingExtensionSpec\$CustomException: exception message   │\n" + 
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘";
	
	
	def CAUGHT_HEADER = 
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" + 
			"║caught                                                                                            ║\n" + 
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝";
	def CAUGHT_FOOTER =
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" + 
			"│caught                                                                                            │\n" + 
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" + 
			"│SUCCESS                                                                                           │\n" + 
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘";
			
	def DATA_TABLE_SUCCESS_HEADERS = [
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" +
			"║success: false and false = false                                                                  ║\n" +
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝",
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" +
			"║success: false and true = false                                                                   ║\n" +
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝",
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" +
			"║success: true and false = false                                                                   ║\n" +
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝",
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" +
			"║success: true and true = true                                                                     ║\n" +
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝",
	]
	def DATA_TABLE_SUCCESS_FOOTERS = [
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" +
			"│success: false and false = false                                                                  │\n" +
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" +
			"│SUCCESS                                                                                           │\n" +
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘",
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" +
			"│success: false and true = false                                                                   │\n" +
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" +
			"│SUCCESS                                                                                           │\n" +
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘",
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" +
			"│success: true and false = false                                                                   │\n" +
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" +
			"│SUCCESS                                                                                           │\n" +
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘",
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" +
			"│success: true and true = true                                                                     │\n" +
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" +
			"│SUCCESS                                                                                           │\n" +
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘"
	]
	
	def DATA_TABLE_FAILURE_HEADERS = [
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" +
			"║failure: false and false = true                                                                   ║\n" +
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝",
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" +
			"║failure: false and true = false                                                                   ║\n" +
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝",
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" +
			"║failure: true and false = true                                                                    ║\n" +
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝",
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" +
			"║failure: true and true = true                                                                     ║\n" +
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝"
	]
	def DATA_TABLE_FAILURE_FOOTERS = [
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" +
			"│failure: false and false = true                                                                   │\n" +
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" +
			"│FAILED                                                                                            │\n" +
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" +
			"│Condition not satisfied:                                                                          │\n" +
			"│                                                                                                  │\n" +
			"│and == expected                                                                                   │\n" +
			"│|   |  |                                                                                          │\n" +
			"│|   |  true                                                                                       │\n" +
			"│|   false                                                                                         │\n" +
			"│false                                                                                             │\n" +
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘",
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" +
			"│failure: false and true = false                                                                   │\n" +
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" +
			"│SUCCESS                                                                                           │\n" +
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘",
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" +
			"│failure: true and false = true                                                                    │\n" +
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" +
			"│FAILED                                                                                            │\n" +
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" +
			"│Condition not satisfied:                                                                          │\n" +
			"│                                                                                                  │\n" +
			"│and == expected                                                                                   │\n" +
			"│|   |  |                                                                                          │\n" +
			"│|   |  true                                                                                       │\n" +
			"│|   false                                                                                         │\n" +
			"│false                                                                                             │\n" +
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘",
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" +
			"│failure: true and true = true                                                                     │\n" +
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" +
			"│SUCCESS                                                                                           │\n" +
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘"
	]

	StringWriter writer;
	
	def setup() {
		writer = new StringWriter();
		TestPrinterFactoryAdapter.setWriter(writer);
	}

	def cleanup() {
		System.setProperty("execute-fake-test-for-testing-logging-extension", "");
		TestPrinterFactoryAdapter.reset();
	}

	def "check success logs"() {
		given:
			System.setProperty("execute-fake-test-for-testing-logging-extension", "success");
		when:
			EngineTestKit.engine("junit-vintage")
				.selectors(selectClass(FakeSpec.class))
				.execute()
					.tests()
						.assertStatistics({s -> s.failed(0).succeeded(1)});
			def logs = writer.toString();
		then:
			logs.contains(SUCCESS_HEADER);
			logs.contains(SUCCESS_FOOTER);
	}
	
	def "check failure logs"() {
		given:
			System.setProperty("execute-fake-test-for-testing-logging-extension", "failure");
		when:
			EngineTestKit.engine("junit-vintage")
				.selectors(selectClass(FakeSpec.class))
				.execute()
					.tests()
						.assertStatistics({s -> s.failed(1).succeeded(0)});
			def logs = writer.toString();
		then:
			logs.contains(FAILURE_HEADER);
			logs.contains(FAILURE_FOOTER);
	}
	
	def "check caught logs"() {
		given:
			System.setProperty("execute-fake-test-for-testing-logging-extension", "caught");
		when:
			EngineTestKit.engine("junit-vintage")
				.selectors(selectClass(FakeSpec.class))
				.execute()
					.tests()
						.assertStatistics({s -> s.failed(0).succeeded(1)});
			def logs = writer.toString();
		then:
			logs.contains(CAUGHT_HEADER);
			logs.contains(CAUGHT_FOOTER);
	}
	
	def "check data table success logs"() {
		given:
			System.setProperty("execute-fake-test-for-testing-logging-extension", "data table success");
		when:
			EngineTestKit.engine("junit-vintage")
				.selectors(selectClass(FakeSpec.class))
				.execute()
					.tests()
						.assertStatistics({s -> s.failed(0).succeeded(4)});
			def logs = writer.toString();
		then:
			DATA_TABLE_SUCCESS_HEADERS.each {
				assert logs.contains(it)
			}
			DATA_TABLE_SUCCESS_FOOTERS.each {
				assert logs.contains(it);
			}
	}
	
	def "check data table failure logs"() {
		given:
			System.setProperty("execute-fake-test-for-testing-logging-extension", "data table failure");
		when:
			EngineTestKit.engine("junit-vintage")
				.selectors(selectClass(FakeSpec.class))
				.execute()
					.tests()
						.assertStatistics({s -> s.failed(2).succeeded(2)});
			def logs = writer.toString();
		then:
			DATA_TABLE_FAILURE_HEADERS.each {
				assert logs.contains(it)
			}
			DATA_TABLE_FAILURE_FOOTERS.each {
				assert logs.contains(it);
			}
	}

	
	@Unroll
	@LogTestInformation(maxLength = 100, marker = "foo", printer = TestPrinterFactoryAdapter.class)
	static class FakeSpec extends Specification {
		
		@Requires({ System.getProperty("execute-fake-test-for-testing-logging-extension", "").equals("success") })
		def "success"() {
			when:
				def foo = "foo"
			then:
				true
		}
		
		@Requires({ System.getProperty("execute-fake-test-for-testing-logging-extension", "").equals("failure") })
		def "failure"() {
			when:
				throw new CustomException("exception message", new IllegalArgumentException("cause message"));
			then:
				true
		}
		
		@Requires({ System.getProperty("execute-fake-test-for-testing-logging-extension", "").equals("caught") })
		def "caught"() {
			when:
				throw new CustomException("exception message", new IllegalArgumentException("cause message"));
			then:
				thrown(CustomException)
		}
		
		@Requires({ System.getProperty("execute-fake-test-for-testing-logging-extension", "").equals("data table success") })
		def "success: #a and #b = #expected"() {
			when:
				def and = a && b
			
			then:
				and == expected
				
			where:
				a     | b     || expected
				false | false || false
				false | true  || false
				true  | false || false
				true  | true  || true
		}

		@Requires({ System.getProperty("execute-fake-test-for-testing-logging-extension", "").equals("data table failure") })
		def "failure: #a and #b = #expected"() {
			when:
				def and = a && b
			
			then:
				and == expected
				
			where:
				a | b || expected
				false | false || true
				false | true  || false
				true  | false || true
				true  | true  || true
		}
	}
	
	public static class CustomException extends Exception {
		public CustomException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
