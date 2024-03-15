package net.unethicalite.motherlodemine;

import com.google.inject.Inject;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

public class S1dMlmOverlay extends OverlayPanel {
    private final S1dMotherlodeMinePlugin plugin;

    @Inject
    public S1dMlmOverlay(S1dMotherlodeMinePlugin plugin) {
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setMinimumSize(180);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        setMinimumSize(180);
        if (plugin.isRunning())
        {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("S-1D MLM")
                    .color(Color.WHITE)
                    .build());


            panelComponent.getChildren().add(LineComponent.builder()
                    .left("State: " + plugin.getActivity())
                    .leftColor(Color.ORANGE)
                    .build());

            //Mining area
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Area: " + plugin.getMiningArea())
                    .leftColor(Color.ORANGE)
                    .build());

            //add empty line
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(" ")
                    .leftColor(Color.WHITE)
                    .build());

            // Level and levels gained
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Deposits left: " + plugin.getRemainingDeposits())
                    .leftColor(Color.RED)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Sack size: " + plugin.curSackSize + "/" + plugin.maxSackSize)
                    .leftColor(Color.GREEN)
                    .build());

        }
        return super.render(graphics);
    }
}
