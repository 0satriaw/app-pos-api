package sawi.saas.pos;

import com.midtrans.Midtrans;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaasPosApplication {

	public static void main(String[] args) {
		//		Midtrans Configuration
		Midtrans.serverKey = "YOUR-CREDENTIAL-KEY";
		Midtrans.isProduction = false;

		SpringApplication.run(SaasPosApplication.class, args);
	}

}
