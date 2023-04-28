package dev.rdh.createunlimited.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.rdh.createunlimited.config.CUConfigScreen;

public class CUModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return CUConfigScreen::new;
    }
}
