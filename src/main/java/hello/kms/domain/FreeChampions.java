package hello.kms.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name="free_champions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeChampions {
    @Id
    @Column(name="champion_id")
    private int championId;
    @Column(name="champion_name")
    private String championName;
}
