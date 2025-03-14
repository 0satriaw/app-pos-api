package sawi.saas.pos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sawi.saas.pos.validation.ValidationGroups;

@Getter
@Setter
public class UserRequest {
    @NotBlank(groups = ValidationGroups.Create.class)
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(groups = ValidationGroups.Create.class, message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ADMIN|CASHIER|OWNER", message = "Invalid role")
    private String role;

}
