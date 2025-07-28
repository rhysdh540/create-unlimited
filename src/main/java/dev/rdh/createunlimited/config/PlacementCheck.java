package dev.rdh.createunlimited.config;

import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

import static com.google.common.base.Predicates.*;

public enum PlacementCheck {
	ON(alwaysTrue()),
	SURVIVAL_ONLY(not(Player::isCreative)),
	OFF(alwaysFalse());

	final Predicate<Player> enabled;

	PlacementCheck(Predicate<Player> enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabledFor(Player player) {
		return enabled.test(player);
	}
}
