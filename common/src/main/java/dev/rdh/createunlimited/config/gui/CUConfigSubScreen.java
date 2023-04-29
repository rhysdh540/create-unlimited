package dev.rdh.createunlimited.config.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.rdh.createunlimited.config.CUConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public class CUConfigSubScreen extends Screen {
    private Screen lastScreen;
    private String category;
    private OptionsList optionsList;

    protected CUConfigSubScreen(String category, Screen parent) {
        super(Component.nullToEmpty("Create Unlimited " + category + " Options"));
        lastScreen = parent;
        this.category = category;
    }
    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }
    @Override
    protected void init() {
        int i = 1;
        ForgeConfigSpec.ConfigValue<?>[] values = CUConfig.getObjects(category);
        for(ForgeConfigSpec.ConfigValue<?> value : values) {
            Button b = new Button(this.width / 2 - 100, i*24 + 32, 200, 20, Component.nullToEmpty("test"), a -> {}, (button, poseStack, x, y) -> {
                Component tooltip = Component.nullToEmpty("test");
                CUConfigSubScreen.this.renderTooltip(poseStack, tooltip, x, y);
            });
        }
        Button doneButton = new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, button -> onClose());
        this.addRenderableWidget(doneButton);
        this.optionsList = new OptionsList(this.minecraft, this.width, this.height, 24, this.height - 32, 25);
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
