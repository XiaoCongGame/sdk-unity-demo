using UnityEngine;
using System.Collections;

public class XcSdkBindings : MonoBehaviour {
	
	#if UNITY_ANDROID
	
	private static XcSdkBindings _instance;
	
	public static XcSdkBindings Instance {
		get {
			if(_instance == null) {
				_instance = new XcSdkBindings();
			}
			return _instance;
		}
	}	
	
	// for register
	public void register() {
		using (AndroidJavaClass unityPlayer = new AndroidJavaClass ("com.unity3d.player.UnityPlayer")) {
			using (AndroidJavaObject curActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity")) {					
				curActivity.Call("register");
			}
		}
	}
	
	// for login
	public void login() {
		using (AndroidJavaClass unityPlayer = new AndroidJavaClass ("com.unity3d.player.UnityPlayer")) {
			using (AndroidJavaObject curActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity")) {					
				curActivity.Call("login");
			}
		}
	}
	
	// for login without cache
	public void changeUser() {
		using (AndroidJavaClass unityPlayer = new AndroidJavaClass ("com.unity3d.player.UnityPlayer")) {
			using (AndroidJavaObject curActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity")) {					
				curActivity.Call("changeUser");
			}
		}
	}
	
	// for payment
	public void pay() {
		using (AndroidJavaClass unityPlayer = new AndroidJavaClass ("com.unity3d.player.UnityPlayer")) {
			using (AndroidJavaObject curActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity")) {					
				curActivity.Call("pay");
			}
		}
	}
	
	#endif
}
