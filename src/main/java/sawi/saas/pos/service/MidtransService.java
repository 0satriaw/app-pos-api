package sawi.saas.pos.service;

import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sawi.saas.pos.dto.MidtransResponse;
import sawi.saas.pos.entity.Order;

import java.math.RoundingMode;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MidtransService {

    @Value("${midtrans.serverKey}")
    private String serverKey;

    @Value("${midtrans.clientKey}")
    private String clientKey;

    @Value("${midtrans.baseUrl}")
    private String baseUrl;

    Logger logger = LoggerFactory.getLogger(MidtransService.class);


    public MidtransResponse createTransaction(Order order, String paymentMethod) {
        try {
            MidtransResponse midtransResponse = new MidtransResponse();


            Map<String, Object> params = new HashMap<>();

            Map<String, String> transactionDetails = new HashMap<>();
            transactionDetails.put("order_id", order.getId().toString());
            transactionDetails.put("gross_amount", order.getTotalPrice().setScale(0, RoundingMode.CEILING).toString());


            params.put("transaction_details", transactionDetails);

            JSONObject responseBody = SnapApi.createTransaction(params);
            logger.info("Transaction created successfully");
            logger.info("Response body: {}", responseBody.toString());

            if(!responseBody.isEmpty() ) {
                if(responseBody.get("token")!=null && responseBody.get("redirect_url")!=null) {
                    midtransResponse.setToken(responseBody.get("token").toString());
                    midtransResponse.setRedirect_url(responseBody.get("redirect_url").toString());

                    return midtransResponse;
                }
            }


        }catch(Exception e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("Failed to create transaction");

    }

    // Create params JSON Raw Object request
    public Map<String, Object> requestBody() {
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
