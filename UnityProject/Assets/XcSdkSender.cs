using UnityEngine;
using System.Collections;

public class XcSdkSender : MonoBehaviour {

	void OnGUI() {
		if(GUILayout.Button("register", GUILayout.Width(200), GUILayout.Height(100))) {
			XcSdkBindings.Instance.register();
		}
		
		if(GUILayout.Button("login", GUILayout.Width(200), GUILayout.Height(100))) {
			XcSdkBindings.Instance.login();
		}
		
		if(GUILayout.Button("changeUser", GUILayout.Width(200), GUILayout.Height(100))) {
			XcSdkBindings.Instance.changeUser();
		}
		
		if(GUILayout.Button("pay", GUILayout.Width(200), GUILayout.Height(100))) {
			XcSdkBindings.Instance.pay();
		}
	}
}
