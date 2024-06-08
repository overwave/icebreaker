package dev.overwave.icebreaker.core.navigation;

import lombok.Builder;
import lombok.Getter;

import java.util.Map.Entry;

@Builder
@Getter
public class IceClassCharacteristics {
    private Entry<MovementType, Float> middleIce;
    private Entry<MovementType, Float> heavyIce;
    private boolean icebreaker;
}
