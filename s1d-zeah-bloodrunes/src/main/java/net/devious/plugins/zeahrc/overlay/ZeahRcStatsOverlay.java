package net.devious.plugins.zeahrc.overlay;


import com.google.inject.Inject;
import net.devious.plugins.zeahrc.ZeahRcPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

public class ZeahRcStatsOverlay extends OverlayPanel
{
    private final ZeahRcPlugin plugin;

    @Inject
    private ZeahRcStatsOverlay(ZeahRcPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPriority(OverlayPriority.MED);
        setMinimumSize(200);
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        setMinimumSize(200);
        ZeahRcSession session = plugin.getSession();

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("S-1D Zeah Blood runes v0.0.4")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Bot time: ")
                .right(session.getElapsedTime())
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Dark altar trips: ")
                .right(session.getAltarTrips())
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Task: ")
                .right(session.getCurrentTask())
                .build());

        return super.render(graphics);
    }
}
