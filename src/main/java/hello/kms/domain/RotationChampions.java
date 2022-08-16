package hello.kms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RotationChampions {
    @JsonProperty("freeChampionIds")
    private List<Integer> freeChampionIds;
    @JsonProperty("freeChampionIdsForNewPlayers")
    private List<Integer> freeChampionIdsForNewPlayers;
    @JsonProperty("maxNewPlayerLevel")
    private int maxNewPlayerLevel;

    private List<String> freeChampionNames;
    private List<String> freeChampionNamesForNewPlayers;
}
