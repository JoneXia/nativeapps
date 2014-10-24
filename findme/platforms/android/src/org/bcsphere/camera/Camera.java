package org.bcsphere.camera;

import java.io.Serializable;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class Camera extends CordovaPlugin implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static boolean isOpened  = false;
	private CallbackContext onCameraClose;
	
	@Override
	public boolean execute(String action, JSONArray args,CallbackContext callbackContext) throws JSONException {
		try {
			if("takePicture".equals(action)){
				String pictureMode = args.getJSONObject(0).getString("model");
				if (pictureMode.equals("0")) {
					if (!isOpened) {
					Intent intent = new Intent();
					intent.setClass(webView.getContext(),TakePictureAty.class);
					intent.putExtra("MODE", 0);
					webView.getContext().startActivity(intent);
					callbackContext.success();
					}
				}else if (pictureMode.equals("1")) {
					if (!isOpened) {
						Intent intent = new Intent();
						intent.setClass(webView.getContext(),TakePictureAty.class);
						intent.putExtra("MODE", 1);
						webView.getContext().startActivity(intent);
					}else {
						Intent intent = new Intent();
						intent.setAction("TAKE_PICTURE");
						webView.getContext().sendBroadcast(intent);
					}
					callbackContext.success();
				}
			}else if("onCameraClose".equals(action)){
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction("ON_CAMERA_CLOSE");
				webView.getContext().registerReceiver(receiver, intentFilter);
				onCameraClose = callbackContext;
			}
			
		} catch (Exception e) {
			callbackContext.error(e.getMessage());
		}
		
		return true;
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if("ON_CAMERA_CLOSE".equals(intent.getAction())){
				if(onCameraClose!=null){
					PluginResult result = new PluginResult(Status.OK);
					result.setKeepCallback(true);
					onCameraClose.sendPluginResult(result);
				}
			}
		}
	};

	public static void setCameraState(boolean state){
		isOpened = state;
	}
	
	
}
