package hro.inflab.dockyou.node;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import hro.inflab.dockyou.node.container.DummyContext;

public class TestNode {
	private Node node;

	@Before
	public void setup() {
		node = new Node(null, new DummyContext());
	}

	@Test
	public void testSettings() {
		Assert.assertTrue(node.getSettings().isEmpty());
		node.handleActions(getSetId(456));
		Assert.assertEquals(456, node.getSettings().get("id"));
		try {
			node.handleActions(getSetId("NaN"));
			Assert.fail();
		} catch(Exception e) {
		}
	}

	private JSONArray getSetId(Object id) {
		return new JSONArray().put(new JSONObject().put("action", "set-id").put("id", id));
	}

	@Test
	public void testNoActions() {
		try {
			node.handleActions(new JSONArray().put(new JSONObject()));
			Assert.fail();
		} catch(Exception e) {
		}
	}

	@Test
	public void testContext() {
		Assert.assertEquals(0, node.getContext().getContainers().length());
		node.handleActions(new JSONArray().put(new JSONObject().put("action", "container").put("add", "test")));
		Assert.assertEquals(1, node.getContext().getContainers().length());
		JSONArray remove = new JSONArray().put(new JSONObject().put("action", "container").put("remove", 0));
		node.handleActions(remove);
		Assert.assertEquals(0, node.getContext().getContainers().length());
		try {
			node.handleActions(new JSONArray().put(new JSONObject().put("action", "container").put("remove", "NaN")));
			Assert.fail();
		} catch(Exception e) {
		}
	}
}
