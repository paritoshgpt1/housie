package io.github.paritoshgpt1.Housie.dto;

import io.github.paritoshgpt1.Housie.model.Player;
import lombok.Data;

import java.util.List;

@Data
public class PlayerDetailsForm {
    List<Player> players;
}
