package com.robot.barrymore.dto;

import java.time.Duration;

public record UserMovementInstruction(
        Action action,
        Duration duration
) {
}
