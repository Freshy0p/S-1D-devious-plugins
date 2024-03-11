package net.unethicalite.fletcher.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Getter
@RequiredArgsConstructor
public enum Mode {
    FLETCHING_SHORTBOW("Fletch Shortbow"),
    // Fletching longbow Mode
    FLETCHING_LONGBOW("Fletch Longbow"),
    // Fletching Shield Mode
    FLETCHING_SHIELD("Fletch Shield"),
    // Fletching Stocks Mode
    FLETCHING_STOCKS("Fletch Stocks"),
    // Fletching shafts Mode
    FLETCHING_SHAFTS("Fletch Shafts"),
    // Stringing longbow Mode
    STRINGING_LONGBOW("String Longbows"),
    // Stringing shortbow Mode
    STRINGING_SHORTBOW("String Shortbows");



    private final String name;
    @Override
    public String toString()
    {
        return name;
    }
}
