package com.robot.barrymore.llmApiClient;

import com.robot.barrymore.dto.Action;
import com.robot.barrymore.dto.UserMovementInstruction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;

public class llmClient {

    public UserMovementInstruction decipherUserInstruction(@NotNull @NotBlank String instruction) {
        //TODO
        return new UserMovementInstruction(
                Action.MOVE_FORWARD,
                Duration.ofSeconds(3)
        );
    }
}
