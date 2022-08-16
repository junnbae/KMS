package hello.kms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RotationChampions {
    @JsonProperty("freeChampionIds")
    private List<Integer> freeChampionIds;
    @JsonProperty("freeChampionIdsForNewPlayers")
    private List<Integer> freeChampionIdsForNewPlayers;
    @JsonProperty("maxNewPlayerLevel")
    private int maxNewPlayerLevel;
}
