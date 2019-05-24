package usr.cxh.canvas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlobTest {
	@Test
	public void testToJson() {
		Arena arena = new Arena(100, 100);
		Blob blob = new Blob(10, 30, arena);
		String s = blob.toJson();
		assertEquals(s, "{\"food\":0.0,\"energy\":100.0,\"size\":10.0,\"speed\":1.0,\"senseRadius\":100.0,\"algae\":10.0,\"_color\":{\"value\":-14785436,\"falpha\":0.0},\"_secondaryColor\":{\"value\":-15382970,\"falpha\":0.0},\"_arena\":{\"blobs\":[],\"food\":[],\"_arenaWidth\":100,\"_arenaHeight\":100,\"winners\":[]},\"strat\":\"ROAM\",\"kills\":0,\"roamTarget\":{\"x\":255.0,\"y\":265.0},\"x\":10.0,\"y\":30.0}");
	}
}
