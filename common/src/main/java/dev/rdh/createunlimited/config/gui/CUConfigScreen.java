package dev.rdh.createunlimited.config.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.AbstractMap;
import java.util.List;

public final class CUConfigScreen extends Screen {
    private Screen lastScreen;
    private OptionsList optionsList;
    private final List<AbstractMap.SimpleImmutableEntry<String, String>> categoryNames = List.of(
            new AbstractMap.SimpleImmutableEntry<>("Trains", "Settings for train limits"),
            new AbstractMap.SimpleImmutableEntry<>("Super Glue", "Settings for glue placing"),
            new AbstractMap.SimpleImmutableEntry<>("Extendo Grip", "Settings for Extendo-Grip reach")
    );
    private Button[] categories = new Button[categoryNames.size()];

    public CUConfigScreen(Screen parent) {
        super(Component.literal("Create Unlimited Options"));
        this.lastScreen = parent;
    }
    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        Button doneButton = new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, button -> onClose());
        this.optionsList = new OptionsList(this.minecraft, this.width, this.height, 24, this.height - 32, 25);
        this.addRenderableWidget(doneButton);
        for(int i = 0; i < categories.length; ++i) {
            AbstractMap.SimpleImmutableEntry<String, String> entry = categoryNames.get(i);
            categories[i] = new Button(this.width / 2 - 100, i*24 + 32, 200, 20, Component.nullToEmpty(entry.getKey() + " Settings"), a ->
                    this.minecraft.setScreen(new CUConfigSubScreen(entry.getKey(), this)),
                (button, poseStack, x, y) -> {
                Component tooltip = Component.nullToEmpty(entry.getValue());
                CUConfigScreen.this.renderTooltip(poseStack, tooltip, x, y);
            });
            this.addRenderableWidget(categories[i]);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        this.optionsList.render(poseStack, mouseX, mouseY, partialTicks);
        drawCenteredString(poseStack, this.font, this.title.getString(),
                this.width / 2, 8, 0xFFFFFF);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
