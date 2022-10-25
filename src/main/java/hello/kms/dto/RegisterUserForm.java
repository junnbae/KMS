package hello.kms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserForm {
    private String userId;
    private String password;
    private String userName;
}
