package dev.overwave.icebreaker.core.ship;

import dev.overwave.icebreaker.core.navigation.MovementType;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Map.Entry;

@RequiredArgsConstructor
public enum IceClass {
    ICE_0_3(
            Map.entry(MovementType.FOLLOWING, 0.15F),
            Map.entry(MovementType.FORBIDDEN, 0F),
            false),
    ARC_4_6(
            Map.entry(MovementType.FOLLOWING, 0.6F),
            Map.entry(MovementType.FOLLOWING, 0.15F),
            false),
    ARC_7(
            Map.entry(MovementType.INDEPENDENT, 0.6F),
            Map.entry(MovementType.FOLLOWING, 0.7F),
            false),
    ARC_9_TAIMIR_VAIGACH(
            Map.entry(MovementType.INDEPENDENT, 0.9F),
            Map.entry(MovementType.INDEPENDENT, 0.75F),
            true),
    ARC_9_50_YEARS_OF_VICTORY_YAMAL(
            Map.entry(MovementType.INDEPENDENT, 1F),
            Map.entry(MovementType.INDEPENDENT, 1F),
            true);

    private final Entry<MovementType, Float> medium;
    private final Entry<MovementType, Float> hard;
    private final boolean icebreaker;

    public Entry<MovementType, Float> getCharacteristics(float integralVelocity, float speed) {
        if (integralVelocity < 10) {
            return Map.entry(MovementType.FORBIDDEN, 0F);
        }
        Entry<MovementType, Float> characteristics;
        if (integralVelocity >= 20) {
            return Map.entry(MovementType.INDEPENDENT, speed);
        } else if (integralVelocity >= 15) {
            characteristics = medium;
        } else {
            characteristics = hard;
        }

        if (icebreaker) {
            return Map.entry(characteristics.getKey(), integralVelocity * characteristics.getValue());
        } else {
            return Map.entry(characteristics.getKey(), speed * characteristics.getValue());
        }
    }
}
