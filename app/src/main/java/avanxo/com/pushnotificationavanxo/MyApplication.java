package avanxo.com.pushnotificationavanxo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.provider.Settings.Secure;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.salesforce.marketingcloud.InitializationStatus;
import com.salesforce.marketingcloud.MCLogListener;
import com.salesforce.marketingcloud.MarketingCloudConfig;
import com.salesforce.marketingcloud.MarketingCloudSdk;
import com.salesforce.marketingcloud.messages.Region;
import com.salesforce.marketingcloud.messages.RegionMessageManager;
import com.salesforce.marketingcloud.messages.geofence.GeofenceMessageResponse;
import com.salesforce.marketingcloud.messages.proximity.ProximityMessageResponse;
import com.salesforce.marketingcloud.notifications.NotificationCustomizationOptions;
import com.salesforce.marketingcloud.notifications.NotificationManager;
import com.salesforce.marketingcloud.notifications.NotificationMessage;
import com.salesforce.marketingcloud.registration.Registration;
import com.salesforce.marketingcloud.registration.RegistrationManager;

import java.util.List;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import avanxo.com.pushnotificationavanxo.data.MCGeofence;
import avanxo.com.pushnotificationavanxo.data.MCLocationManager;

public class MyApplication extends Application implements MarketingCloudSdk.InitializationListener,
        RegistrationManager.RegistrationEventListener, NotificationManager.NotificationBuilder,
        RegionMessageManager.GeofenceMessageResponseListener{

    private static final String TAG = "MyApplication";

    /**
     * Set to true to show how geo fencing works within the SDK.
     */
    public static final boolean LOCATION_ENABLED = true;

    /**
     * Set to true to show how beacons messages works within the SDK.
     */
    public static final boolean PROXIMITY_ENABLED = false;

    @Override public void onCreate() {
        super.onCreate();
        String deviceId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        Log.e(TAG, "onCreate() deviceId: " + deviceId);

        //Log de Mkt Cloud
        MarketingCloudSdk.setLogLevel(MCLogListener.VERBOSE);
        MarketingCloudSdk.setLogListener(new MCLogListener.AndroidLogListener());

        MarketingCloudSdk.init(this, MarketingCloudConfig.builder()
            //Autenticación
            .setApplicationId("8b59a9b1-faff-4a34-8c41-bcbbe4510abe") // ENTER YOUR MARKETING CLOUD APPLICATION ID HERE
            .setAccessToken("v24mfftrbgcnwpq3pwrqpxab") // ENTER YOUR MARKETING CLOUD ACCESS TOKEN HERE
            .setSenderId("272732178929") // ENTER YOUR GOOGLE SENDER ID HERE
            .setMarketingCloudServerUrl("https://mcf05xpz3c3xhlmnhrcwdr-ct5m4.device.marketingcloudapis.com/")
            .setMid("7276982")

            //Habilitando otro tipo de comunicaciones
            .setGeofencingEnabled(LOCATION_ENABLED)
            .setProximityEnabled(PROXIMITY_ENABLED)

            //personalización
            .setNotificationCustomizationOptions(
                    NotificationCustomizationOptions.create(R.drawable.ic_launcher_background)
            )
            // Other configuration options
            .setNotificationCustomizationOptions(NotificationCustomizationOptions.create(R.drawable.ic_stat_app_logo_transparent))
            .build(this), this);

        MarketingCloudSdk.requestSdk(new MarketingCloudSdk.WhenReadyListener() {
            @Override
            public void ready(MarketingCloudSdk sdk) {
                sdk.getRegistrationManager().registerForRegistrationEvents(MyApplication.this);
                sdk.getRegionMessageManager().registerGeofenceMessageResponseListener(MyApplication.this);
                //sdk.getRegionMessageManager().registerProximityMessageResponseListener(MyApplication.this);
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

    @Override
    public NotificationCompat.Builder setupNotificationBuilder(@NonNull Context context, @NonNull NotificationMessage notificationMessage) {
        NotificationCompat.Builder builder = NotificationManager.getDefaultNotificationBuilder(context, notificationMessage, NotificationManager.createDefaultNotificationChannel(context), R.drawable.ic_stat_app_logo_transparent);

        Map<String, String> customKeys = notificationMessage.customKeys();
        if (!customKeys.containsKey("category") || !customKeys.containsKey("sale_date")) {
            return builder;
        }

        if ("sale".equalsIgnoreCase(customKeys.get("category"))) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            try {
                Date saleDate = simpleDateFormat.parse(customKeys.get("sale_date"));
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, saleDate.getTime())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, saleDate.getTime())
                        .putExtra(CalendarContract.Events.TITLE, customKeys.get("event_title"))
                        .putExtra(CalendarContract.Events.DESCRIPTION, customKeys.get("alert"))
                        .putExtra(CalendarContract.Events.HAS_ALARM, 1)
                        .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                //PendingIntent pendingIntent = PendingIntent.getActivity(context, R.id.interactive_notification_reminder, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                //builder.addAction(android.R.drawable.ic_menu_my_calendar, getString(R.string.in_btn_add_reminder), pendingIntent);
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return builder;
    }


    /**
     * Listens for a GeofenceResponses.
     * <p/>
     * This event retrieves the data related to geolocations
     * and saves them as a list of MCGeofence in MCLocationManager
     */
    @Override
    public void onGeofenceMessageResponse(GeofenceMessageResponse response) {
        MarketingCloudSdk.getInstance().getAnalyticsManager().trackPageView("data://GeofenceResponseEvent", "Geofence Response Event Received");
        List<Region> regions = response.fences();
        for (Region r : regions) {
            MCGeofence newLocation = new MCGeofence();
            LatLng latLng = new LatLng(r.center().latitude(), r.center().longitude());
            newLocation.setCoordenates(latLng);
            newLocation.setRadius(r.radius());
            newLocation.setName(r.name());
            MCLocationManager.getInstance().getGeofences().add(newLocation);
        }
    }
}
