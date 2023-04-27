package net.rdh.createunlimited;

import com.simibubi.create.Create;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is the main class for the mod. It is responsible for initializing the mod and providing some utility functions.
 * @author Rhys de Haan
 */
public class CreateUnlimited {
    /**
     * The mod's ID, used in code to identify it.
     */
    public static final String MOD_ID = "createunlimited";
    /**
     * The mod's human-readable name.
     */
    public static final String NAME = "Create Unlimited";
    /**
     * The mod's logger, used to send messages to the {@code latest.log} file with our mod's name.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    /**
     * Initializes the mod.
     * This is called from the {@code onInitialize()} method on Fabric/Quilt, and the {@code CreateUnlimitedForge} constructor on Forge.
     * All it really does though is print a message to the log.
     */
    public static void init() {
        LOGGER.info("{} initializing! Create version: {} on platform: {}", NAME, Create.VERSION, CUExpectPlatform.platformName());
    }

    /**
     * Creates a {@link ResourceLocation} with the mod's ID as the namespace.
     * Used for loading assets into our mod (which has none, so it's unused right now).
     * @param path The path of the resource location.
     * @return A {@link ResourceLocation} with the mod's ID as the namespace and the given path.
     */
    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    /**
     * Computes the intersection point of two line segments defined by two points and two direction vectors in a given plane.
     * Similar to {@link com.simibubi.create.foundation.utility.VecHelper#intersect(Vec3, Vec3, Vec3, Vec3, Direction.Axis) VecHelper.intersect()} but unable to return {@code null}, to prevent {@link NullPointerException}s from occuring.<p>
     * If the plane is X-axis, the input points and direction vectors are projected onto the YZ plane.
     * If the plane is Z-axis, the input points and direction vectors are projected onto the XY plane.
     * @param p1 the first endpoint of the first line segment
     * @param p2 the second endpoint of the first line segment
     * @param r the direction vector of the first line segment
     * @param s the direction vector of the second line segment
     * @param plane the plane in which the line segments are defined, specified by an element of the enum Direction.Axis
     * @return a {@code double} array containing the values of {@code t} and {@code u}, the parameters that define the intersection point: <br> {@code P1 + tR = P2 + uS}
     */
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
        return new double[] { t, u };
    }

    /**
     * Creates a new {@link ItemStack} that is a copy of the given stack, but with the specified size.
     * If the given size is zero, an empty {@code ItemStack} is returned.
     *
     * @param stack the stack to copy
     * @param size the size of the new stack
     * @return a new {@code ItemStack} with the same properties as the given stack, but with the specified size
     */
    public static ItemStack copyStackWithSize(ItemStack stack, int size) {
        if (size == 0) return ItemStack.EMPTY;
        ItemStack copy = stack.copy();
        copy.setCount(size);
        return copy;
    }
}
