package dev.rdh.createunlimited.config;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class CUConfigScreen extends Screen {
    private Screen parent;
    public CUConfigScreen() {
        super(Component.literal("Create Unlimited Options"));
    }
    public CUConfigScreen(Screen parent) {
        this();
        this.parent = parent;
    }
    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(PoseStack poseStack,
                       int mouseX, int mouseY, float partialTicks) {
        // First draw the background of the screen
        this.renderBackground(poseStack);
        // Draw the title
        drawCenteredString(poseStack, this.font, this.title.getString(),
                this.width / 2, 8, 0xFFFFFF);
        // Call the super class' method to complete rendering
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
