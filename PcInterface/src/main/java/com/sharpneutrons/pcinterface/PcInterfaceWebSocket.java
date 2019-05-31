package com.sharpneutrons.pcinterface;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sharpneutrons.pcinterface.message.Message;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

public class PcInterfaceWebSocket extends NanoWSD.WebSocket {

	private PcInterface pcInterface;

	PcInterfaceWebSocket(NanoHTTPD.IHTTPSession handshakeRequest, PcInterface pcInterface) {
		super(handshakeRequest);
		this.pcInterface = pcInterface;
		Log.i(PcInterface.TAG   , "PcInterfaceWebSocket: created");
	}

	@Override
	protected void onOpen() {
		Log.i(PcInterface.TAG, "[OPEN] " + this.getHandshakeRequest().getRemoteIpAddress());
		pcInterface.addSocket(this);
	}

	@Override
	protected void onClose(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiateByRemote) {
		Log.i(PcInterface.TAG, "onClose: code: " + code + " " + this.getHandshakeRequest().getRemoteIpAddress());
		pcInterface.removeSocket(this);
	}

	@Override
	protected void onMessage(NanoWSD.WebSocketFrame message) {
		byte[] bytes = message.getBinaryPayload();
		Message msg = new Message(bytes);
		//Log.i(PcInterface.TAG, "onMessage: " + (bytes[1] & 0xff));
		pcInterface.onMessage(this, msg);
	}

	@Override
	protected void onPong(NanoWSD.WebSocketFrame pong) {

	}

	@Override
	protected void onException(IOException e) {}

	public void send(Message message) {
		//try {
			//String messageStr = GSON.toJson(message);
			//Log.i(PcInterface.TAG, "[SENT]: " + messageStr);
			//send(messageStr);
		//} catch (IOException e) {
		//	Log.w(PcInterface.TAG, e);
		//}
	}

}
