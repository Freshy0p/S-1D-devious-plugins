package net.unethicalite.fletcher.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Material
{
    LOG("Log"),
    OAK("Oak"),
    WILLOW("Willow"),
    MAPLE("Maple"),
    YEW("Yew"),
    MAGIC("Magic"),
    REDWOOD("Redwood");

    private final String name;


    @Override
    public String toString()
    {
        return name;
    }
}