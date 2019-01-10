package avanxo.com.pushnotificationavanxo;

import android.app.Application;
import android.support.annotation.NonNull;

import android.provider.Settings.Secure;
import android.util.Log;

import com.salesforce.marketingcloud.InitializationStatus;
import com.salesforce.marketingcloud.MarketingCloudConfig;
import com.salesforce.marketingcloud.MarketingCloudSdk;
import com.salesforce.marketingcloud.notifications.NotificationCustomizationOptions;
import com.salesforce.marketingcloud.registration.Registration;
import com.salesforce.marketingcloud.registration.RegistrationManager;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Locale;

public class MyApplication extends Application implements MarketingCloudSdk.InitializationListener,
        RegistrationManager.RegistrationEventListener
        {
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

            @Override
            public void complete(InitializationStatus status) {
                if (!status.isUsable()) {
                    Log.e(TAG, "Marketing Cloud Sdk init failed.", status.unrecoverableException());
                } else {
                    MarketingCloudSdk cloudSdk = MarketingCloudSdk.getInstance();
                    cloudSdk.getAnalyticsManager().trackPageView("data://ReadyAimFireCompleted", "Marketing Cloud SDK Initialization Complete");

                    if (status.locationsError()) {
                        final GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
                        Log.i(TAG, String.format(Locale.ENGLISH, "Google Play Services Availability: %s", googleApiAvailability.getErrorString(status.playServicesStatus())));
                        if (googleApiAvailability.isUserResolvableError(status.playServicesStatus())) {
                            googleApiAvailability.showErrorNotification(MyApplication.this, status.playServicesStatus());
                        }
                    }
                }
            }

            @Override
            public void onRegistrationReceived(@NonNull Registration registration) {
                MarketingCloudSdk.getInstance().getAnalyticsManager().trackPageView("data://RegistrationEvent", "Registration Event Completed");
                Log.d(TAG, registration.toString());
                Log.d(TAG, String.format("Last sent: %1$d", System.currentTimeMillis()));
            }
        }
