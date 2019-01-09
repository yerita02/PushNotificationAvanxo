package avanxo.com.pushnotificationavanxo;

import android.app.Application;
import android.support.annotation.NonNull;

import android.provider.Settings.Secure;
import android.util.Log;

import com.salesforce.marketingcloud.InitializationStatus;
import com.salesforce.marketingcloud.MarketingCloudConfig;
import com.salesforce.marketingcloud.MarketingCloudSdk;
import com.salesforce.marketingcloud.notifications.NotificationCustomizationOptions;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    @Override public void onCreate() {

        super.onCreate();

        String deviceId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

        Log.e(TAG, "onCreate() deviceId: " + deviceId);

        MarketingCloudSdk.init(this, MarketingCloudConfig.builder()
                .setApplicationId("8b59a9b1-faff-4a34-8c41-bcbbe4510abe") // ENTER YOUR MARKETING CLOUD APPLICATION ID HERE
                .setAccessToken("v24mfftrbgcnwpq3pwrqpxab") // ENTER YOUR MARKETING CLOUD ACCESS TOKEN HERE
                .setSenderId("272732178929") // ENTER YOUR GOOGLE SENDER ID HERE
                .setMarketingCloudServerUrl("https://mcf05xpz3c3xhlmnhrcwdr-ct5m4.device.marketingcloudapis.com/")
                //.setMid("7276982")
                .setNotificationCustomizationOptions(
                        NotificationCustomizationOptions.create(R.drawable.ic_launcher_background)
                )
// Other configuration options
                .build(this), new MarketingCloudSdk.InitializationListener() {
            @Override public void complete(@NonNull InitializationStatus status) {
// TODO handle initialization status
            }
        });
    }
}
