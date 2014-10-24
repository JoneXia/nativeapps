package org.bcsphere.telephony;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;


public class Telephony extends CordovaPlugin{
	
	private CallbackContext callsRemindingCallbackContext;
	
	private TelephonyManager telephonyManager; 

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		// TODO Auto-generated method stub
		super.initialize(cordova, webView);
		telephonyManager = (TelephonyManager)webView.getContext().getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	@Override
	public boolean execute(String action, JSONArray args,CallbackContext callbackContext) throws JSONException {
		
		if("callsReminding".equals(action)){
			telephonyManager.listen(new PhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
			callsRemindingCallbackContext = callbackContext;
		}
		
		return true;
	}
	
	private class PhoneListener extends PhoneStateListener { 
        @Override
        public void onCallStateChanged(int state, String incomingNumber) { 
            super.onCallStateChanged(state, incomingNumber); 
             
		   if(state == TelephonyManager.CALL_STATE_RINGING){
			   if(callsRemindingCallbackContext!=null){
				   JSONObject jsonObject= new JSONObject();
		    	   try {
					   jsonObject.put("incomingNumber", incomingNumber);
					   jsonObject.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		   } catch (JSONException e) {
				   // TODO Auto-generated catch block
					   e.printStackTrace();
				   }
		    	   PluginResult pluginResult = new PluginResult(Status.OK, jsonObject);
				   pluginResult.setKeepCallback(true);
		    	   callsRemindingCallbackContext.sendPluginResult(pluginResult);
			   }
		   }
        } 
         
    } 
}
