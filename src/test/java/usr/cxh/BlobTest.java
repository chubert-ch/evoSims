package usr.cxh.canvas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlobTest {
	@Test
	public void testToJson() {
		Blob blob = new Blob(10, 30);
		String s = blob.toJson();
		assertEquals(s, "{\"food\":0.0,\"energy\":100.0,\"size\":10.0,\"speed\":1.0,\"senseRadius\":100.0,\"algae\":10.0,\"_color\":{\"value\":-14785436,\"falpha\":0.0},\"_secondaryColor\":{\"value\":-15382970,\"falpha\":0.0},\"strat\":\"ROAM\",\"kills\":0,\"roamTarget\":{\"x\":255.0,\"y\":265.0},\"x\":10.0,\"y\":30.0}");
	}
}
