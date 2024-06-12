package dev.overwave.icebreaker.core.ship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IceClass {
    NO_ICE(IceClassGroup.ICE_0_3, "Без ледового класса", "No ice"),
    ICE_1(IceClassGroup.ICE_0_3, "Ice1 (ЛУ1)", "Ice1"),
    ICE_2(IceClassGroup.ICE_0_3, "Ice2 (ЛУ2)", "Ice2"),
    ICE_3(IceClassGroup.ICE_0_3, "Ice3 (ЛУ3)", "Ice3"),
    ARC_4(IceClassGroup.ARC_4_6, "Arc4 (ЛУ4)", "Arc4"),
    ARC_5(IceClassGroup.ARC_4_6, "Arc5 (ЛУ5)", "Arc5"),
    ARC_6(IceClassGroup.ARC_4_6, "Arc6 (ЛУ6)", "Arc6"),
    ARC_7(IceClassGroup.ARC_7_8, "Arc7 (ЛУ7)", "Arc7"),
    ARC_8(IceClassGroup.ARC_7_8, "Arc8 (ЛУ8)", "Arc8"),
    ARC_9_TAIMIR_VAIGACH(IceClassGroup.ARC_9_TAIMIR_VAIGACH, "Arc9 (ЛУ9) — Таймыр / Вайгач", "Arc9"),
    ARC_9_50_YEARS_OF_VICTORY_YAMAL(IceClassGroup.ARC_9_50_YEARS_OF_VICTORY_YAMAL,
            "Arc9 (ЛУ9) — 50 лет Победы / Ямал", "Arc9"),
    ;

    private final IceClassGroup group;
    private final String description;
    private final String shortDescription;
}
