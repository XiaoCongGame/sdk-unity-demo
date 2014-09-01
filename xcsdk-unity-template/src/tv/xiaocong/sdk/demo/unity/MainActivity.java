package tv.xiaocong.sdk.demo.unity;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tv.xiaocong.sdk.security.LoginActivity;
import tv.xiaocong.sdk.security.RegisterActivity;
import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;
import com.xiaocong.sdk.PaymentResults;
import com.xiaocong.sdk.pay.PaymentActivity;
import com.xiaocong.sdk.pay.PaymentHelper;

/**
 * The main activity for the demo.
 * 
 * @author yaoyuan
 * 
 */
public class MainActivity extends UnityPlayerActivity {

    private static final int REQUEST_CODE_LOGIN = 1;

    private static final int REQUEST_CODE_REGISTER = 2;

    private static final int REQUEST_CODE_SPLASH = 3;

    public void register() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);

        startActivityForResult(registerIntent, REQUEST_CODE_REGISTER);
    }

    public void login() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        // MUST be string !!!!!
        loginIntent.putExtra("client_id", String.valueOf(Keys.APP_ID));
        loginIntent.putExtra("client_secret", Keys.APP_KEY);
        loginIntent.putExtra("usingCache", true); // 记住登录

        startActivityForResult(loginIntent, REQUEST_CODE_LOGIN);
    }

    public void changeUser() {
        LoginActivity.startMe(this, REQUEST_CODE_LOGIN, String.valueOf(Keys.APP_ID), Keys.APP_KEY, true);
    }

    public void pay() {
        final int amount = 10; // 10 cents/ 10 xiaocong coins

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        final String orderNo = Keys.APP_ID + df.format(new Date());

        final String goodsDes = "A sword";

        final String sign = getSign(amount, orderNo);

        final String remark = "This is a test payment";

        pay(this, amount, orderNo, goodsDes, sign, remark, null);
    }

    public void pay2() {
        final int amount = 100;

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        final String orderNo = Keys.APP_ID + df.format(new Date());

        final String goodsDes = "10 swords";

        final String sign = getSign(amount, orderNo);

        final String remark = "This is a test payment";

        pay(this, amount, orderNo, goodsDes, sign, remark, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK && data != null) {
            afterLogin(data);
        } else if (requestCode == REQUEST_CODE_REGISTER) {
            afterRegiter(resultCode, data);
        } else if (requestCode == REQUEST_CODE_SPLASH) {
            Toast.makeText(this, "Game started", Toast.LENGTH_LONG).show();
        } else if (requestCode == PaymentActivity.REQUEST_CODE_START_PAY) {
            afterPaying(resultCode);
        }
    }

    private void afterPaying(int resultCode) {
        if (resultCode == PaymentResults.PAYRESULT_OK) {
            Toast.makeText(this, "I got a sword!!!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Result: " + resultCode, Toast.LENGTH_LONG).show();
        }

        UnityPlayer.UnitySendMessage("XcSdkReceiver", "onPay", String.valueOf(resultCode));
    }

    private void afterRegiter(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String username = data.getStringExtra(RegisterActivity.USERNAME);
            Toast.makeText(this, username, Toast.LENGTH_LONG).show();

            UnityPlayer.UnitySendMessage("XcSdkReceiver", "onRegister", username);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();

            UnityPlayer.UnitySendMessage("XcSdkReceiver", "onRegister", "");
        } else {
            Toast.makeText(this, "Failed to register!", Toast.LENGTH_LONG).show();

            UnityPlayer.UnitySendMessage("XcSdkReceiver", "onRegister", "");
        }

    }

    private void afterLogin(Intent data) {
        String accessToken = data.getStringExtra(LoginActivity.RESPONSE_ACCESS_TOKEN);
        String username = data.getStringExtra(LoginActivity.USERNAME);

        String toast = String.format("access_token: %s, username: %s", accessToken, username);
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();

        UnityPlayer.UnitySendMessage("XcSdkReceiver", "onLogin", accessToken);

        // Optional: If you want get the unique identifier of the user, you
        // could:
        // UserBasicInfo userInfo =
        // XcServiceClient.getUserBasicInfo(accessToken);
        // userInfo.getId();
    }

    /**
     * Execute payment.
     * 
     * @param amount
     *            (Required) the money to pay. The unit is RMB cent. (1 Xiaocong
     *            coin == 1 RMB cent.)
     * @param orderNo
     *            (Required) the order number in your system. Should be unique
     *            for all your request. Prefixed by your partnerId. Format:
     *            ^{12,30}$.
     * @param goodsDes
     *            (Required) some descriptions about your goods
     * @param signature
     *            (Required) the request signature. For the format of the
     *            signature, refer to
     *            {@link MainActivity#getSign(int, int, String, String, String)}
     *            .
     * @param remark
     *            (Optional) some remark for this order
     * @param accessToken
     *            (Optional) If you don't want users to change their account,
     *            provide a accessToken yourself; If you pass null, then we'll
     *            pop up login dialog to get the accessToken if necessary.
     */
    private static void pay(Activity caller, int amount, String orderNo, String goodsDes, String signature,
            String remark, String accessToken) {
        PaymentHelper.startMe(caller, //
                Keys.APP_ID, //
                amount,//
                "md5", // only md5 supported now
                orderNo, //
                Keys.PACKAGE_NAME, goodsDes, //
                signature, //
                Keys.NOTIFY_URL, //
                remark, //
                String.valueOf(Keys.APP_ID), // for back-compatible
                Keys.APP_KEY, //
                accessToken);
    }

    /** Build the request signature. */
    private static String getSign(int amount, String orderNo) throws RuntimeException {
        try {
            return md5code(Keys.APP_ID + "&" + Keys.PACKAGE_NAME + "&" + amount + "&" + orderNo + "&" + Keys.APP_KEY);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Generate MD5-encoded code */
    private static String md5code(String srcStr) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(srcStr.getBytes());
        byte[] mdbytes = md.digest();

        // convert the byte to hex format method 1
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
