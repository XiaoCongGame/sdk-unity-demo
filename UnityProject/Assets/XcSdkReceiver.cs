using UnityEngine;
using System.Collections;

public class XcSdkReceiver : MonoBehaviour {

	public void onLogin(string accessToken) {
		Debug.Log("Login, token = " + accessToken);
	}

	public void onPay(string resultCode) {
		Debug.Log("Pay, result = " + resultCode);
	}

	public void onRegister(string username) {
		Debug.Log("Register, username = " + username);
	}

}

