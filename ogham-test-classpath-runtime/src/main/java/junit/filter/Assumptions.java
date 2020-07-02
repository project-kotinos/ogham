package junit.filter;

import static org.junit.Assume.assumeTrue;		

public class Assumptions {
	public static void requires(String... requiredFacets) {
		for (String requiredFacet : requiredFacets) {
			assumeTrue(hasFacet(requiredFacet));
		}
	}
	
	private static boolean hasFacet(String facet) {
		String[] activeFacets = System.getProperty("activeFacets").split(",");
		for (String activeFacet : activeFacets) {
			if (facet.equals(activeFacet)) {
				return true;
			}
		}
		return false;
	}
	
	private Assumptions() {
		super();
	}
}
