package hello.kms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserForm {
    private String userId;
    private String password;
}
