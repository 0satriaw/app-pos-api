package sawi.saas.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MidtransResponse {
    private String token;
    private String redirect_url;
}
