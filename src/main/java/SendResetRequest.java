import requests.Request;

import java.util.Map;
import java.util.UUID;

public class SendResetRequest {
    private Request request;
    Map<String, String> headers = Map.of(
                "User-Agent", "Instagram 150.0.0.0.000 Android (29/10; 300dpi; 720x1440;",
                "Content-Type", "application/x-www-form-urlencoded"
        );
    Map<String, String> data;
    public SendResetRequest(String username) {
        this.data = Map.of(
                "_csrftoken", Generator.getRandomString(32),
                "username", username,
                "guid", String.valueOf(UUID.randomUUID()),
                "device_id", String.valueOf(UUID.randomUUID())
        );
        this.request = new Request("https://i.instagram.com/api/v1/accounts/send_password_reset/",
                "POST",
                headers,
                data
                );
    }

    public Request getRequest() {
        return request;
    }
}
