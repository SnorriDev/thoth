package tests.grammar;

import org.junit.jupiter.api.Test;

import snorri.grammar.ChartParser;
import snorri.grammar.DefaultLexicon;
import snorri.semantics.commands.Command;

class ChartParserTest {
	
	static {
		DefaultLexicon.load();
	}
	
	@Test
	void testWpi() {
		Command cmd = ChartParser.parseText("wpi");
		assert cmd != null;
	}

	@Test
	void testQmaSnbt() {
		Command cmd = ChartParser.parseText("qmA snbt");
		assert cmd != null;
	}

	@Test
	void testBmDrDmiTnAnx() {
		Command cmd = ChartParser.parseText("bm Dr dmi tn anx");
		assert cmd != null;
	}

	@Test
	void testBmDrDmiTnAnxElseSmRSt() {
		Command cmd = ChartParser.parseText("bm Dr dmi tn anx ELSE Sm r st");
		assert cmd != null;
	}

}
