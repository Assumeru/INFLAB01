package hro.inflab.dockyou.node;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import hro.inflab.dockyou.node.container.DummyContext;
import hro.inflab.dockyou.node.hb.HeartBeatListener;

public class TestHeartBeat {
	private Node node;
	private HeartBeatListener listener;

	@Before
	public void setup() {
		node = new Node(null, new DummyContext());
		listener = new HeartBeatListener(node, 0xD0CC);
		node.handleActions(TestNode.getSetId(123));
	}

	@Test
	public void test() throws Exception {
		listener.start();
		Assert.assertTrue(listener.isRunning());
		JSONObject message = new JSONObject(readHeartBeat());
		Assert.assertEquals(123, message.getInt("id"));
		Assert.assertEquals(0, message.getJSONArray("containers").length());
		Assert.assertEquals(0, message.getJSONArray("starting").length());
		node.handleActions(new JSONArray().put(new JSONObject().put("action", "container").put("add", "test")));
		message = new JSONObject(readHeartBeat());
		Assert.assertEquals(123, message.getInt("id"));
		Assert.assertEquals(1, message.getJSONArray("containers").length());
		Assert.assertEquals(0, message.getJSONArray("starting").length());
		listener.stop();
		Assert.assertFalse(listener.isRunning());
	}

	private String readHeartBeat() throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL("http://localhost:53452").openConnection();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try(InputStream in = conn.getInputStream()) {
			byte[] buffer = new byte[2048];
			int read;
			while((read = in.read(buffer)) > 0) {
				out.write(buffer, 0, read);
			}
		}
		return out.toString();
	}
}
