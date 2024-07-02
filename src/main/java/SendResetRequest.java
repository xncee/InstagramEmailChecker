import requests.Request;

import java.util.Map;
import java.util.UUID;

public class SendResetRequest {
    Request request;
    public SendResetRequest(String username) {
        Map<String, String> headers = Map.of(
                "User-Agent", "Instagram 150.0.0.0.000 Android (29/10; 300dpi; 720x1440;",
                "Content-Type", "application/x-www-form-urlencoded"
        );
//        Map<String, String> data = Map.of(
//                "phone_id", String.valueOf(UUID.randomUUID()),
//                "q", username,
//                "guid", String.valueOf(UUID.randomUUID()),
//                "device_id", String.valueOf(UUID.randomUUID()),
//                "android_build_type", "release",
//                "waterfall_id", String.valueOf(UUID.randomUUID()),
//                "directly_sign_in", "true",
//                "is_wa_installed", "false"
//        );
//        request = new Request("https://i.instagram.com/api/v1/users/lookup/",
//                "POST",
//                headers,
//                data
//                );
        Map<String, String> data = Map.of(
                "_csrftoken", Generator.getRandomString(32),
                "username", username,
                "guid", String.valueOf(UUID.randomUUID()),
                "device_id", String.valueOf(UUID.randomUUID())
        );
        request = new Request("https://i.instagram.com/api/v1/accounts/send_password_reset/",
                "POST",
                headers,
                data
                );
    }

    public Request getRequest() {
        return request;
    }
}
