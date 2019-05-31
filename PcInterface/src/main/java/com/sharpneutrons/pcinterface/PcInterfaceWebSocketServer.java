package com.sharpneutrons.pcinterface;

import android.content.Context;
import android.util.Log;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

public class PcInterfaceWebSocketServer extends NanoWSD {

	private static final int PORT = 5873;

	private Context ctx;

	private PcInterface pcInterface;

	PcInterfaceWebSocketServer(PcInterface pcInterface, Context ctx) {
		super(PORT);
		this.pcInterface = pcInterface;
		this.ctx = ctx;
	}

	@Override
	protected WebSocket openWebSocket(IHTTPSession handshake) {
		return new PcInterfaceWebSocket(handshake, pcInterface);
	}

//	@Override
//	public Response serve(IHTTPSession session) {
////		Map<String, String> parms = session.getParms();
////		String param = parms.get("params");
////		String action = parms.get("action");
////		String delay = parms.get("delay");
//		try {
//			return newChunkedResponse(Response.Status.OK, MIME_HTML, ctx.getAssets().open("pcInterface.html"));
//		} catch (IOException e) {
//			Log.e(PcInterface.TAG, "server: ", e);
//			return newFixedLengthResponse(Response.Status.NOT_FOUND,
//					MIME_PLAINTEXT, "404 Not found");
//		}
//	}

}
