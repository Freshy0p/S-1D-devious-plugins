package net.unethicalite.fletcher;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

public class S1dFletcherOverlay extends OverlayPanel
{
    private final Client client;
    private final S1dFletcherPlugin plugin;
    private final S1dFletcherConfig config;

    @Inject
    private S1dFletcherOverlay(Client client, S1dFletcherPlugin plugin, S1dFletcherConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setMinimumSize(180);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        setMinimumSize(180);
        if (plugin.isScriptStarted())
        {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("S-1D Fletcher")
                    .color(Color.WHITE)
                    .build());

            //Add a center line
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Running: " + plugin.getTimeRunning())
                    .color(Color.WHITE)
                    .build());



            panelComponent.getChildren().add(LineComponent.builder()
                    .left("State: " + plugin.getActivity())
                    .leftColor(Color.ORANGE)
                    .build());
            //Mode
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Mode: " + plugin.getMode())
                    .leftColor(Color.ORANGE)
                    .build());
            //add empty line
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(" ")
                    .leftColor(Color.WHITE)
                    .build());

            // Level and levels gained
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Level (gained): " + plugin.getClient().getRealSkillLevel(Skill.FLETCHING) + " (" + plugin.getLevelsGained() + ")")
                    .leftColor(Color.RED)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("XP gained: " + plugin.getXpGained() + " (" + (plugin.getXpPerHour()/1000) + "k/hr)")
                    .leftColor(Color.GREEN)
                    .build());


            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time to next Level: " + plugin.getFormattedTimeToLevel())
                    .leftColor(Color.WHITE)
                    .build());

            //add empty line
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(" ")
                    .leftColor(Color.WHITE)
                    .build());

                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Fletched: " + plugin.getLogsFletched())
                        .leftColor(Color.GREEN)
                        .build());


            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Times banked: " + plugin.getTimesBanked())
                    .leftColor(Color.GREEN)
                    .build());
        }
        return super.render(graphics);
    }
}
