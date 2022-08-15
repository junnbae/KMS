package hello.kms.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Summoner {
    private String type;
    private String timeStamp;
    private String result;
    private String champion;
    private int kill;
    private int death;
    private int assist;
}
