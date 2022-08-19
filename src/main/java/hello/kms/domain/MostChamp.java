package hello.kms.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MostChamp {
    private String Champion;
    private double Kda;
    private int count;
    private int WinRate;
}
