package com.sharpneutrons.pcinterface;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.sharpneutrons.pcinterface.message.Message;
import com.sharpneutrons.pcinterface.message.MessageDeserializer;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

public class PcInterfaceWebSocket extends NanoWSD.WebSocket {

	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Message.class, new MessageDeserializer())
			.create();

	private PcInterface pcInterface;

	PcInterfaceWebSocket(NanoHTTPD.IHTTPSession handshakeRequest, PcInterface pcInterface) {
		super(handshakeRequest);
		this.pcInterface = pcInterface;
	}

	@Override
	protected void onOpen() {
		Log.i(PcInterface.TAG, "[OPEN] " + this.getHandshakeRequest().getRemoteIpAddress());
		pcInterface.addSocket(this);
	}

	@Override
	protected void onClose(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiateByRemote) {
		Log.i(PcInterface.TAG, "onClose: " + this.getHandshakeRequest().getRemoteIpAddress());
		pcInterface.removeSocket(this);
	}

	@Override
	protected void onMessage(NanoWSD.WebSocketFrame message) {
		Message msg = GSON.fromJson(message.getTextPayload(), Message.class);
		Log.i(PcInterface.TAG, "onMessage: " + message.getTextPayload());
		pcInterface.onMessage(this, msg);
	}

	@Override
	protected void onPong(NanoWSD.WebSocketFrame pong) {}

	@Override
	protected void onException(IOException e) {}

	public void send(Message message) {
		try {
			String messageStr = GSON.toJson(message);
			Log.i(PcInterface.TAG, "[SENT]: " + messageStr);
			send(messageStr);
		} catch (IOException e) {
			Log.w(PcInterface.TAG, e);
		}
	}

}
