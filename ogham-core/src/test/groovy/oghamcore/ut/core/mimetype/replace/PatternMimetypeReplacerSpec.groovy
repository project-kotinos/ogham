package oghamcore.ut.core.mimetype.replace;

import fr.sii.ogham.core.mimetype.replace.PatternMimetypeReplacer
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class PatternMimetypeReplacerSpec extends Specification {

	def "replace application/xhtml by text/html pattern rule applied on #mimetype shoud be #expected"() {
		expect:
			new PatternMimetypeReplacer("application/xhtml[^;]*(;.*)?", "text/html\$1").replace(mimetype).equals(expected)

		where:
			mimetype                              || expected
			"foo"                                 || "foo"
			"application/xhtml"                   || "text/html"
			"application/xhtml;charset=UTF-8"     || "text/html;charset=UTF-8"
			"foo"                                 || "foo"
			"application/xhtml+xml"               || "text/html"
			"application/xhtml+xml;charset=UTF-8" || "text/html;charset=UTF-8"
	}
}
