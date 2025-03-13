package sawi.saas.pos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    @NotBlank
    @Email(message = "Invalid email format")
    private String email;

    private String password;
    private String name;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ADMIN|CASHIER|OWNER", message = "Invalid role")
    private String role;

}
