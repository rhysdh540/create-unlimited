package dev.rdh.createunlimited.asm.mixin.glue;


import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.Set;

@Mixin(SuperGlueSelectionHelper.class)
public abstract class SuperGlueSelectionHelperMixin {
	/**
	 * @author rdh
	 * @reason test
	 */
	@Overwrite
	public static Set<BlockPos> searchGlueGroup(Level level, BlockPos startPos, BlockPos endPos, boolean includeOther) {
		if(endPos == null || startPos == null) {
			return null;
		}

		AABB bb = new AABB(startPos, endPos);
		Set<BlockPos> blocks = new ObjectOpenHashSet<>();
		for(double posx = bb.minX; posx <= bb.maxX; posx++) {
			for(double posy = bb.minY; posy <= bb.maxY; posy++) {
				for(double posz = bb.minZ; posz <= bb.maxZ; posz++) {
					BlockPos pos = new BlockPos((int) posx, (int) posy, (int) posz);
					Block block = level.getBlockState(pos).getBlock();

					if(block != Blocks.AIR && block != Blocks.WATER) {
						blocks.add(pos);
					}
				}
			}
		}

		return blocks;
	}
}
