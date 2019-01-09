package avanxo.com.pushnotificationavanxo;

import android.app.Application;
import android.support.annotation.NonNull;

import com.salesforce.marketingcloud.InitializationStatus;
import com.salesforce.marketingcloud.MarketingCloudConfig;
import com.salesforce.marketingcloud.MarketingCloudSdk;
import com.salesforce.marketingcloud.notifications.NotificationCustomizationOptions;

public class MyApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();

        MarketingCloudSdk.init(this, MarketingCloudConfig.builder()
                .setApplicationId("8adb5388-8199-4f68-b963-c097a4756426") // ENTER YOUR MARKETING CLOUD APPLICATION ID HERE
                .setAccessToken("6csmwu8jcgarwq2zz6m5p5yz") // ENTER YOUR MARKETING CLOUD ACCESS TOKEN HERE
                .setSenderId("821996846887") // ENTER YOUR GOOGLE SENDER ID HERE
                //.setMarketingCloudServerUrl("{marketing_cloud_url}")
                //.setMid("{mid}")
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
