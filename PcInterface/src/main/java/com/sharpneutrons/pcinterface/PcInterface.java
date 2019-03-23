package com.sharpneutrons.pcinterface;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.sharpneutrons.pcinterface.message.Message;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.android.dex.TableOfContents;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.robotcore.internal.webserver.MimeTypesUtil;
import org.firstinspires.ftc.robotcore.internal.webserver.WebHandler;
import org.firstinspires.ftc.robotcore.internal.webserver.WebHandlerManager;
import org.firstinspires.ftc.robotcore.internal.webserver.WebServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;

public class PcInterface implements OpModeManagerImpl.Notifications{

	public static final String TAG = "PcInterface";

	private static PcInterface instance;

	/**
	 * Starts the instance and a WebSocket server that listens for external connections
	 */
	public static void start(Context context) {
		if (instance == null) {
			instance = new PcInterface(context);
		}
	}

	/**
	 * Attaches a web server for accessing the interface through the phone (similar to OBJ)
	 * @param webServer the web server
	 */
	public static void attachWebServer(WebServer webServer) {
		instance.internalAttachWebServer(webServer);
	}

	/**
	 * Attaches the event loop to the instance for op mode management.
	 * @param eventLoop the event loop
	 */
	public static void attachEventLoop(EventLoop eventLoop) {
		instance.internalAttachEventLoop(eventLoop);
	}

	/**
	 * Stops the instace and the underlying WebSocket server
	 */
	public static void stop() {
		if (instance != null) {
			instance.close();
			instance = null;
		}
	}

	/**
	 * Returns the active instance. This should be called after {@link #start(Context)}.
	 * @return active instance or null while outside its lifecycle
	 */
	public static PcInterface getInstace() {
		return instance;
	}

	private List<PcInterfaceWebSocket> sockets;
	private PcInterfaceWebSocketServer server;
	private OpModeManagerImpl opModeManager;
	private AssetManager assetManager;
	private List<String> assetFiles;

	private PcInterface(Context context) {
		sockets = new ArrayList<>();

		//TODO see if classpath is necessary

		server = new PcInterfaceWebSocketServer(this, context);
		try {
			server.start();
			Log.i(TAG, "PcInterface: server started");
		} catch (IOException e) {
			Log.w(TAG, "PcInterface:" +e);
		}

		assetManager = context.getAssets();
		assetFiles = new ArrayList<>();
		buildAssetsFileList("pcInterface");
	}

	private WebHandler newStaticAssetHandler(final String file) {
		return new WebHandler() {
			@Override
			public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) throws IOException,
					NanoHTTPD.ResponseException {
				if(session.getMethod() == NanoHTTPD.Method.GET) {
					String mimeType = MimeTypesUtil.determineMimeType(file);
					return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, mimeType,
							assetManager.open(file));
				}else {
					return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND,
							NanoHTTPD.MIME_PLAINTEXT, "Not Found");
				}
			}
		};
	}

	private boolean buildAssetsFileList(String path) {
		try {
			String[] list = assetManager.list(path);
			if (list == null) return false;
			if (list.length > 0) {
				for (String file : list) {
					if (!buildAssetsFileList(path + "/" + file)) {
						return false;
					}
				}
			} else {
				assetFiles.add(path);
			}
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	/**
	 * I think this just adds all of the files in the /pcInterface directory to the web server
	 * @param webServer
	 */
	private void internalAttachWebServer(WebServer webServer) {
		WebHandlerManager manager = webServer.getWebHandlerManager();
		manager.register("", newStaticAssetHandler("index.html"));
		manager.register("/pcInterface", newStaticAssetHandler("index.html"));

		manager.register("/pcInterface", newStaticAssetHandler("pcInterface/index.html"));
		manager.register("/pcInterface/", newStaticAssetHandler("pcInterface/index.html"));
		for (final String file : assetFiles) {
			manager.register("/" + file, newStaticAssetHandler(file));
		}
	}

	private void internalAttachEventLoop(EventLoop eventLoop) {
		//this method may be called multiple times, so make sure it doesn't attach too many listeners
		if (opModeManager != null) {
			opModeManager.unregisterListener(this);
		}

		opModeManager = eventLoop.getOpModeManager();
		if (opModeManager != null) {
			opModeManager.registerListener(this);
		}

		//TODO the rest of this would add the opmodes to the opmode list
	}

	private synchronized void sendAll(Message message) {
		for (PcInterfaceWebSocket ws : sockets) {
			ws.send(message);
		}
	}

	synchronized void addSocket(PcInterfaceWebSocket socket) {
		sockets.add(socket);

		//TODO would send config json messages to the socket
		//TODO would also send opmode list
	}

	synchronized void removeSocket(PcInterfaceWebSocket socket) {
		sockets.remove(socket);
	}

	synchronized void onMessage(PcInterfaceWebSocket socket, Message msg) {
		Telemetry telemetry = opModeManager.getActiveOpMode().telemetry;

		switch (msg.getType()) {
			case JOYSTICK_DATA: {
				//TODO might have to be something different to get the actual data
				telemetry.addData("Joystick: ", msg.getData().toString());
			}
			case KINECT_SKELETON: {
				telemetry.addData("Kinect Skeleton: ", msg.getData().toString());
			}
		}
	}

	private void close() {
		if (opModeManager != null) {
			opModeManager.unregisterListener(this);
		}
		server.stop();
	}

	@Override
	public void onOpModePreInit(OpMode opMode) {}

	@Override
	public void onOpModePreStart(OpMode opMode) {}

	@Override
	public void onOpModePostStop(OpMode opMode) {}
}
