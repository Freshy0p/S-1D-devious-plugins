package net.unethicalite.tempoross;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;

public class HootTemporossOverlay extends Overlay
{
    private final Client client;
    private final S1dSoloTemporossPlugin plugin;
    private final S1dSoloTemporossConfig config;

    @Inject
    private HootTemporossOverlay(Client client, S1dSoloTemporossPlugin plugin, S1dSoloTemporossConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        return null;
    }
}
