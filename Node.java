package in.mspkapps100.Dairy_of_ccare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
@RestController
public class Node {

    private static final String ACCOUNT_SID = "ACd61381720a5534459d03e614e8728c13";
    private static final String AUTH_TOKEN = "12864d154edde28691d835c88afc5055";
    private static final String TWILIO_PHONE_NUMBER = "+918270426785";
    private static final Map<String, String> otps = new HashMap<>(); // In-memory storage for OTPs

    public static void main(String[] args) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        SpringApplication.run(OtpApplication.class, args);
        System.out.println("Server running on port 3000");
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phone_number");
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // Generate 6-digit OTP
        otps.put(phoneNumber, otp); // Save OTP for the phone number

        try {
            Message message = Message.creator(new PhoneNumber(phoneNumber),
                    new PhoneNumber(TWILIO_PHONE_NUMBER),
                    "Your OTP is " + otp).create();
            return ResponseEntity.ok(Map.of("success", true, "message", "OTP sent"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to send OTP"));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phone_number");
        String otp = request.get("otp");

        if (otps.get(phoneNumber) != null && otps.get(phoneNumber).equals(otp)) {
            return ResponseEntity.ok(Map.of("success", true, "message", "OTP verified successfully"));
        } else {
            return ResponseEntity.ok(Map.of("success", false, "message", "Invalid OTP"));
        }
    }
}

