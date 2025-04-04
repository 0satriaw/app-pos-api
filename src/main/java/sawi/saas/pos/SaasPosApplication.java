package sawi.saas.pos;

import com.midtrans.Midtrans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaasPosApplication {

	public static void main(String[] args) {
		Logger log = LoggerFactory.getLogger(SaasPosApplication.class);
		//		Midtrans Configuration
		Midtrans.serverKey = "SB-Mid-server-ZdB_mQ8wQGcK9dA1pkTn9okM";
		Midtrans.isProduction = false;

		SpringApplication.run(SaasPosApplication.class, args);
	}

}
