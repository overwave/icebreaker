package dev.overwave.icebreaker.core.ship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IceClass {
    NO_ICE(IceClassGroup.ICE_0_3),
    ICE_1(IceClassGroup.ICE_0_3),
    ICE_2(IceClassGroup.ICE_0_3),
    ICE_3(IceClassGroup.ICE_0_3),
    ARC_3(IceClassGroup.ARC_4_6),
    ARC_4(IceClassGroup.ARC_4_6),
    ARC_5(IceClassGroup.ARC_4_6),
    ARC_6(IceClassGroup.ARC_4_6),
    ARC_7(IceClassGroup.ARC_4_6),
    ARC_8(IceClassGroup.ARC_4_6),
    ARC_9_TAIMIR_VAIGACH(IceClassGroup.ARC_9_TAIMIR_VAIGACH),
    ARC_9_50_YEARS_OF_VICTORY_YAMAL(IceClassGroup.ARC_9_50_YEARS_OF_VICTORY_YAMAL),
    ;

    private final IceClassGroup group;
}
