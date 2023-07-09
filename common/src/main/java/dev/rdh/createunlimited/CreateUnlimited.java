package dev.rdh.createunlimited;

import com.simibubi.create.Create;

import dev.rdh.createunlimited.command.CreateUnlimitedCommands;
import dev.rdh.createunlimited.config.CUConfig;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUnlimited {
	public static final String MOD_ID = "createunlimited";
	public static final String NAME = "Create Unlimited";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static void init() {
		LOGGER.info("{} initializing! Create version: {} on platform: {}", NAME, Create.VERSION, CUPlatformFunctions.platformName());
		CUConfig.init(CUPlatformFunctions.getConfigDirectory().resolve("createunlimited-IGNOREME.toml"));
		CreateUnlimitedCommands.registerConfigCommand();
	}

	@SuppressWarnings("SuspiciousNameCombination") // javac doesn't like when we pass a value called "y" to a method that expects a value called "x"
	public static double[] intersect(Vec3 p1, Vec3 p2, Vec3 r, Vec3 s, Direction.Axis plane) {
		if (plane == Direction.Axis.X) {
			p1 = new Vec3(p1.y, 0, p1.z);
			p2 = new Vec3(p2.y, 0, p2.z);
			r = new Vec3(r.y, 0, r.z);
			s = new Vec3(s.y, 0, s.z);
		}

		if (plane == Direction.Axis.Z) {
			p1 = new Vec3(p1.x, 0, p1.y);
			p2 = new Vec3(p2.x, 0, p2.y);
			r = new Vec3(r.x, 0, r.y);
			s = new Vec3(s.x, 0, s.y);
		}

		Vec3 qminusp = p2.subtract(p1);
		double rcs = r.x * s.z - r.z * s.x;
		Vec3 rdivrcs = r.scale(1 / rcs);
		Vec3 sdivrcs = s.scale(1 / rcs);
		double t = qminusp.x * sdivrcs.z - qminusp.z * sdivrcs.x;
		double u = qminusp.x * rdivrcs.z - qminusp.z * rdivrcs.x;
		return new double[]{t, u};
	}

	public static ItemStack copyStackWithSize(ItemStack stack, int size) {
		if (size == 0) return ItemStack.EMPTY;
		ItemStack copy = stack.copy();
		copy.setCount(size);
		return copy;
	}
}
