package hello.kms.dto;

import lombok.Getter;
import lombok.Setter;

@Data
public class LoginUserForm {
    private String userId;
    private String password;
}
