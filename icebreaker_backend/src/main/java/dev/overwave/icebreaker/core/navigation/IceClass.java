package dev.overwave.icebreaker.core.navigation;

import java.util.Map;
import java.util.Map.Entry;

public enum IceClass {
    ICE_0_3(IceClassCharacteristics.builder()
            .middleIce(Map.entry(MovementType.FOLLOWING, 0.15F))
            .heavyIce(Map.entry(MovementType.FORBIDDEN, 0F))
            .icebreaker(false)
            .build()),
    ARC_4_6(IceClassCharacteristics.builder()
            .middleIce(Map.entry(MovementType.FOLLOWING, 0.6F))
            .heavyIce(Map.entry(MovementType.FOLLOWING, 0.15F))
            .icebreaker(false)
            .build()),
    ARC_7(IceClassCharacteristics.builder()
            .middleIce(Map.entry(MovementType.INDEPENDENT, 0.6F))
            .heavyIce(Map.entry(MovementType.FOLLOWING, 0.7F))
            .icebreaker(false)
            .build()),
    ARC_9_50_YEARS_OF_VICTORY_YAMAL(IceClassCharacteristics.builder()
            .middleIce(Map.entry(MovementType.INDEPENDENT, 1F))
            .heavyIce(Map.entry(MovementType.INDEPENDENT, 1F))
            .icebreaker(true)
            .build()),
    ARC_9_TAIMIR_VAIGACH(IceClassCharacteristics.builder()
            .middleIce(Map.entry(MovementType.INDEPENDENT, 0.9F))
            .heavyIce(Map.entry(MovementType.INDEPENDENT, 0.75F))
            .icebreaker(true)
            .build());


    private final IceClassCharacteristics iceClassCharacteristics;

    IceClass(IceClassCharacteristics iceClassCharacteristics) {
        this.iceClassCharacteristics = iceClassCharacteristics;
    }

    public Entry<MovementType, Float> getCharacteristics(float integralVelocity, float speed) {
        Entry<MovementType, Float> characteristics;
        boolean pureWater = false;

        if (integralVelocity >= 20) {
            characteristics = Map.entry(MovementType.INDEPENDENT, 1F);
            pureWater = true;
        } else if (integralVelocity >= 15) {
            characteristics = iceClassCharacteristics.getMiddleIce();
        } else {
            characteristics = iceClassCharacteristics.getHeavyIce();
        }

        if (iceClassCharacteristics.isIcebreaker()) {
            if (pureWater) {
                return Map.entry(characteristics.getKey(), speed);
            }
            return Map.entry(characteristics.getKey(), integralVelocity * characteristics.getValue());
        } else {
            return Map.entry(characteristics.getKey(), speed * characteristics.getValue());
        }
    }

}
