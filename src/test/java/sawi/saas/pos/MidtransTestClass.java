package sawi.saas.pos;

import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class MidtransTestClass {

    public static void main(String[] args) throws MidtransError {
        Midtrans.serverKey = "SB-Mid-server-ZdB_mQ8wQGcK9dA1pkTn9okM";
        Midtrans.isProduction = false;



        // Create Token and then you can send token variable to FrontEnd,
        // to initialize Snap JS when customer click pay button
        JSONObject transactionToken = SnapApi.createTransaction(requestBody());

        System.out.println(transactionToken.toString());

        System.out.println("Successfully created transaction token");
    }

    // Create params JSON Raw Object request
    public static Map<String, Object> requestBody() {
        UUID idRand = UUID.randomUUID();
        Map<String, Object> params = new HashMap<>();

        Map<String, String> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", idRand.toString());
        transactionDetails.put("gross_amount", "265000");

        Map<String, String> creditCard = new HashMap<>();
        creditCard.put("secure", "true");

        params.put("transaction_details", transactionDetails);
        params.put("credit_card", creditCard);

        return params;
    }
}

