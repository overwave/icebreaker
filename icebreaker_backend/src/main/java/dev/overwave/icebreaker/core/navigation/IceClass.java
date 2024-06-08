package dev.overwave.icebreaker.core.navigation;

import java.util.Map;
import java.util.Map.Entry;

public enum IceClass {
    NO_ICE_CLASS(IceClassCharacteristics.builder()
            .characteristics1(Map.entry(MovementType.INDEPENDENT, 0F))
            .characteristics2(Map.entry(MovementType.FOLLOWING, 0F))
            .characteristics3(Map.entry(MovementType.FORBIDDEN, 0F))
            .icebreaker(false)
            .build()),
    ICE_1(IceClassCharacteristics.builder()
            .characteristics1(Map.entry(MovementType.INDEPENDENT, 0F))
            .characteristics2(Map.entry(MovementType.FOLLOWING, 0F))
            .characteristics3(Map.entry(MovementType.FORBIDDEN, 0F))
            .icebreaker(false)
            .build()),
    ICE_2(IceClassCharacteristics.builder()
            .characteristics1(Map.entry(MovementType.INDEPENDENT, 0F))
            .characteristics2(Map.entry(MovementType.FOLLOWING, 0F))
            .characteristics3(Map.entry(MovementType.FORBIDDEN, 0F))
            .icebreaker(false)
            .build()),
    ICE_3(IceClassCharacteristics.builder()
            .characteristics1(Map.entry(MovementType.INDEPENDENT, 0F))
            .characteristics2(Map.entry(MovementType.FOLLOWING, 0F))
            .characteristics3(Map.entry(MovementType.FORBIDDEN, 0F))
            .icebreaker(false)
            .build()),
    ARC_4(IceClassCharacteristics.builder()
            .characteristics1(Map.entry(MovementType.INDEPENDENT, 1F))
            .characteristics2(Map.entry(MovementType.FOLLOWING, 0.8F))
            .characteristics3(Map.entry(MovementType.FOLLOWING, 0.7F))
            .icebreaker(false)
            .build()),
    ARC_5(IceClassCharacteristics.builder()
            .characteristics1(Map.entry(MovementType.INDEPENDENT, 1F))
            .characteristics2(Map.entry(MovementType.FOLLOWING, 0.8F))
            .characteristics3(Map.entry(MovementType.FOLLOWING, 0.7F))
            .icebreaker(false)
            .build()),
    ARC_6(IceClassCharacteristics.builder()
            .characteristics1(Map.entry(MovementType.INDEPENDENT, 1F))
            .characteristics2(Map.entry(MovementType.FOLLOWING, 0.8F))
            .characteristics3(Map.entry(MovementType.FOLLOWING, 0.7F))
            .icebreaker(false)
            .build()),
    ARC_7(IceClassCharacteristics.builder()
            .characteristics1(Map.entry(MovementType.INDEPENDENT, 1F))
            .characteristics2(Map.entry(MovementType.INDEPENDENT, 0.6F))
            .characteristics3(Map.entry(MovementType.FOLLOWING, 0.15F))
            .icebreaker(false)
            .build()),
    ARC_9_50_YEARS_OF_VICTORY_YAMAL(IceClassCharacteristics.builder()
            .characteristics1(Map.entry(MovementType.INDEPENDENT, 1F))
            .characteristics2(Map.entry(MovementType.INDEPENDENT, 1F))
            .characteristics3(Map.entry(MovementType.INDEPENDENT, 1F))
            .icebreaker(true)
            .build()),
    ARC_9_TAIMIR_VAIGACH(IceClassCharacteristics.builder()
            .characteristics1(Map.entry(MovementType.INDEPENDENT, 1F))
            .characteristics2(Map.entry(MovementType.INDEPENDENT, 0.9F))
            .characteristics3(Map.entry(MovementType.INDEPENDENT, 0.75F))
            .icebreaker(true)
            .build());


    private final IceClassCharacteristics iceClassCharacteristics;

    IceClass(IceClassCharacteristics iceClassCharacteristics) {
        this.iceClassCharacteristics = iceClassCharacteristics;
    }

    public Entry<MovementType, Float> getCharacteristics(float integralVelocity, float speed) {
        Entry<MovementType, Float> characteristics;

        if (integralVelocity >= 20) {
            characteristics = iceClassCharacteristics.getCharacteristics1();
        } else if (integralVelocity >= 15) {
            characteristics = iceClassCharacteristics.getCharacteristics2();
        } else {
            characteristics = iceClassCharacteristics.getCharacteristics3();
        }

        if (iceClassCharacteristics.isIcebreaker()) {
            return Map.entry(characteristics.getKey(), integralVelocity * characteristics.getValue());
        } else {
            return Map.entry(characteristics.getKey(), speed * characteristics.getValue());
        }
    }

}
