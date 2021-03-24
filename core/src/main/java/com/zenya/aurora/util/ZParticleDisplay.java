package com.zenya.aurora.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * By default the particle xyz offsets and speed aren't 0, but
 * everything will be 0 by default in this class.
 * Particles are spawned to a location. So all the nearby players can see it.
 * <p>
 * The fields of this class are publicly accessible for ease of use.
 * All the fields can be null except the particle type.
 * <p>
 * For cross-version compatibility, instead of Bukkit's {@link org.bukkit.Color}
 * the java awt {@link Color} class is used.
 * <p>
 * the data field is used to store special particle data, such as colored particles.
 * For colored particles a float list is used since the particle size is a float.
 * The format of float list data for a colored particle is:
 * <code>[r, g, b, size]</code>
 *
 * @author Crypto Morin, Zenya4 (modded)
 * @version 5.0.0 (ZParticleDisplay 1.0.0)
 * @see ZParticle
 */

public class ZParticleDisplay implements Cloneable {
    private static final boolean ISFLAT = ZParticle.getParticle("FOOTSTEP") == null;
    private static final Particle DEFAULT_PARTICLE = Particle.CLOUD;

    @Nonnull
    public Particle particle;
    @Nullable
    public Location location;
    @Nullable
    public Callable<Location> locationCaller;
    public int count;
    public double offsetx, offsety, offsetz;
    public double extra;
    @Nullable
    public Vector rotation;
    @Nullable
    public Object data;
    public Player[] players;

    /**
     * Make a new instance of particle display.
     * The position of each particle will be randomized positively and negatively by the offset parameters on each axis.
     *
     * @param particle the particle to spawn.
     * @param location the location to spawn the particle at.
     * @param count    the count of particles to spawn.
     * @param offsetx  the x offset.
     * @param offsety  the y offset.
     * @param offsetz  the z offset.
     * @param extra    in most cases extra is the speed of the particles.
     */
    public ZParticleDisplay(@Nonnull Particle particle, @Nullable Callable<Location> locationCaller, @Nullable Location location, int count, double offsetx, double offsety, double offsetz, double extra, Player... players) {
        this.particle = particle;
        this.location = location;
        this.locationCaller = locationCaller;
        this.count = count;
        this.offsetx = offsetx;
        this.offsety = offsety;
        this.offsetz = offsetz;
        this.extra = extra;
        this.players = players;
    }

    public ZParticleDisplay(@Nonnull Particle particle, @Nullable Location location, int count, double offsetx, double offsety, double offsetz, Player... players) {
        this(particle, null, location, count, offsetx, offsety, offsetz, 0, players);
    }

    public ZParticleDisplay(@Nonnull Particle particle, @Nullable Location location, int count, Player... players) {
        this(particle, location, count, 0, 0, 0, players);
    }

    public ZParticleDisplay(@Nonnull Particle particle, @Nullable Location location, Player... players) {
        this(particle, location, 0, players);
    }

    /**
     * Builds a simple ZParticleDisplay object with cross-version
     * compatible {@link org.bukkit.Particle.DustOptions} properties.
     *
     * @param location the location of the display.
     * @param size     the size of the dust.
     * @return a redstone colored dust.
     * @see #simple(Location, Particle, Player...)
     * @since 1.0.0
     */
    @Nonnull
    public static ZParticleDisplay colored(@Nullable Location location, int r, int g, int b, float size, Player... players) {
        ZParticleDisplay dust = new ZParticleDisplay(Particle.REDSTONE, null, location, 1, 0, 0, 0, 0, players);
        dust.data = new float[]{r, g, b, size};
        return dust;
    }

    /**
     * Builds a simple ZParticleDisplay object with cross-version
     * compatible {@link org.bukkit.Particle.DustOptions} properties.
     *
     * @param location the location of the display.
     * @param color    the color of the particle.
     * @param size     the size of the dust.
     * @return a redstone colored dust.
     * @see #colored(Location, int, int, int, float, Player...)
     * @since 3.0.0
     */
    @Nonnull
    public static ZParticleDisplay colored(@Nullable Location location, @Nonnull Color color, float size, Player... players) {
        return colored(location, color.getRed(), color.getGreen(), color.getBlue(), size, players);
    }

    /**
     * Builds a simple ZParticleDisplay object.
     * An invocation of this method yields exactly the same result as the expression:
     * <p>
     * <blockquote>
     * new ZParticleDisplay(particle, location, 1, 0, 0, 0, 0);
     * </blockquote>
     *
     * @param location the location of the display.
     * @param particle the particle of the display.
     * @return a simple ZParticleDisplay.
     * @since 1.0.0
     */
    @Nonnull
    public static ZParticleDisplay simple(@Nullable Location location, @Nonnull Particle particle, @Nonnull Player... players) {
        Objects.requireNonNull(particle, "Cannot build ZParticleDisplay with null particle");
        return new ZParticleDisplay(particle, null, location, 1, 0, 0, 0, 0, players);
    }

    /**
     * A quick access method to display a simple particle.
     * An invocation of this method yields the same result as the expression:
     * <p>
     * <blockquote>
     * ZParticleDisplay.simple(location, particle).spawn();
     * </blockquote>
     *
     * @param location the location of the particle.
     * @param particle the particle to show.
     * @return a simple ZParticleDisplay.
     * @since 1.0.0
     */
    @Nonnull
    public static ZParticleDisplay display(@Nonnull Location location, @Nonnull Particle particle, @Nonnull Player... players) {
        Objects.requireNonNull(location, "Cannot display particle in null location");
        ZParticleDisplay display = simple(location, particle, players);
        display.spawn();
        return display;
    }

    /**
     * Builds particle settings from a configuration section.
     *
     * @param location the location to display this particle.
     * @param config   the config section for the settings.
     * @return a parsed ZParticleDisplay.
     * @since 1.0.0
     */
    public static ZParticleDisplay fromConfig(@Nullable Location location, @Nonnull ConfigurationSection config) {
        ZParticleDisplay display = new ZParticleDisplay(DEFAULT_PARTICLE, location);
        return edit(display, config);
    }

    /**
     * Builds particle settings from a configuration section.
     *
     * @param display the particle display settings to update.
     * @param config  the config section for the settings.
     * @return an edited ZParticleDisplay.
     * @since 5.0.0
     */
    @Nonnull
    public static ZParticleDisplay edit(@Nonnull ZParticleDisplay display, @Nonnull ConfigurationSection config) {
        Objects.requireNonNull(display, "Cannot edit a null particle display");
        Objects.requireNonNull(config, "Cannot parse ZParticleDisplay from a null config section");

        String particleName = config.getString("particle");
        Particle particle = particleName == null ? null : ZParticle.getParticle(particleName);
        int count = config.getInt("count");
        double extra = config.getDouble("extra");

        if (particle != null) display.particle = particle;
        if (count != 0) display.withCount(count);
        if (extra != 0) display.extra = extra;

        String offset = config.getString("offset");
        if (offset != null) {
            String[] offsets = StringUtils.split(StringUtils.deleteWhitespace(offset), ',');
            if (offsets.length >= 3) {
                double offsetx = NumberUtils.toDouble(offsets[0]);
                double offsety = NumberUtils.toDouble(offsets[1]);
                double offsetz = NumberUtils.toDouble(offsets[2]);
                display.offset(offsetx, offsety, offsetz);
            }
        }

        String rotation = config.getString("rotation");
        if (rotation != null) {
            String[] rotations = StringUtils.split(StringUtils.deleteWhitespace(rotation), ',');
            if (rotations.length >= 3) {
                double x = NumberUtils.toDouble(rotations[0]);
                double y = NumberUtils.toDouble(rotations[1]);
                double z = NumberUtils.toDouble(rotations[2]);
                display.rotation = new Vector(x, y, z);
            }
        }

        String color = config.getString("color");
        if (color != null) {
            String[] colors = StringUtils.split(StringUtils.deleteWhitespace(color), ',');
            if (colors.length >= 3) {
                display.data = new float[]{
                        // Color
                        NumberUtils.toInt(colors[0]), NumberUtils.toInt(colors[1]), NumberUtils.toInt(colors[2]),
                        // Size
                        (colors.length > 3 ? NumberUtils.toFloat(colors[3]) : 1.0f)};
            }
        }

        return display;
    }

    /**
     * We don't want to use {@link Location#clone()} since it doens't copy to constructor and Javas clone method
     * is known to be inefficient and broken.
     *
     * @since 3.0.3
     */
    @Nonnull
    private static Location cloneLocation(@Nonnull Location location) {
        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public String toString() {
        return "ParticleDisplay:[Particle=" + particle + ", Count=" + count + ", Offset:{" + offsetx + ", " + offsety + ", " + offsetz + "}, Extra=" + extra
                + ", Data=" + (data == null ? "null" : data instanceof float[] ? Arrays.toString((float[]) data) : data);
    }

    /**
     * Changes the particle count of the particle settings.
     *
     * @param count the particle count.
     * @return the same particle display.
     * @since 3.0.0
     */
    @Nonnull
    public ZParticleDisplay withCount(int count) {
        this.count = count;
        return this;
    }

    /**
     * In most cases extra is the speed of the particles.
     *
     * @param extra the extra number.
     * @return the same particle display.
     * @since 3.0.1
     */
    @Nonnull
    public ZParticleDisplay withExtra(double extra) {
        this.extra = extra;
        return this;
    }

    /**
     * Adds color properties to the particle settings.
     *
     * @param color the RGB color of the particle.
     * @param size  the size of the particle.
     * @return a colored particle.
     * @see #colored(Location, Color, float, Player...)
     * @since 3.0.0
     */
    @Nonnull
    public ZParticleDisplay withColor(@Nonnull Color color, float size) {
        this.data = new float[]{color.getRed(), color.getGreen(), color.getBlue(), size};
        return this;
    }

    /**
     * Saves an instance of an entity to track the location from.
     *
     * @param entity the entity to track the location from.
     * @return the location tracker entity.
     * @since 3.1.0
     */
    @Nonnull
    public ZParticleDisplay withEntity(@Nullable Entity entity) {
        return withLocationCaller(entity::getLocation);
    }

    /**
     * Sets a caller for location changes.
     *
     * @param locationCaller the caller to call to get the new location.
     * @return the same particle settings with the caller added.
     * @since 3.1.0
     */
    @Nonnull
    public ZParticleDisplay withLocationCaller(@Nullable Callable<Location> locationCaller) {
        this.locationCaller = locationCaller;
        return this;
    }

    /**
     * Gets the location of an entity if specified or the constant location.
     *
     * @return the location of the particle.
     * @since 3.1.0
     */
    @Nullable
    public Location getLocation() {
        try {
            return locationCaller == null ? location : locationCaller.call();
        } catch (Exception e) {
            e.printStackTrace();
            return location;
        }
    }

    /**
     * Gets the players to send the particle to.
     *
     * @return the players to send the particle to.
     * @since ZParticleDisplay 1.0.0
     */
    @Nullable
    public Player[] getPlayers() {
        return players;
    }

    /**
     * Adjusts the rotation settings to face the entitys direction.
     * Only some of the shapes support this method.
     *
     * @param entity the entity to face.
     * @return the same particle display.
     * @since 3.0.0
     */
    @Nonnull
    public ZParticleDisplay faceEntity(@Nonnull Entity entity) {
        Objects.requireNonNull(entity, "Cannot face null entity");
        Location loc = entity.getLocation();
        this.rotation = new Vector(Math.toRadians(loc.getPitch() + 90), Math.toRadians(-loc.getYaw()), 0);
        return this;
    }

    /**
     * Clones the location of this particle display and adds xyz.
     *
     * @param x the x to add to the location.
     * @param y the y to add to the location.
     * @param z the z to add to the location.
     * @return the cloned location.
     * @see #clone()
     * @since 1.0.0
     */
    @Nullable
    public Location cloneLocation(double x, double y, double z) {
        return location == null ? null : cloneLocation(location).add(x, y, z);
    }

    /**
     * Clones this particle settings and adds xyz to its location.
     *
     * @param x the x to add.
     * @param y the y to add.
     * @param z the z to add.
     * @return the cloned ZParticleDisplay.
     * @see #clone()
     * @since 1.0.0
     */
    @Nonnull
    public ZParticleDisplay cloneWithLocation(double x, double y, double z) {
        ZParticleDisplay display = clone();
        if (location == null) return display;
        display.location.add(x, y, z);
        return display;
    }

    /**
     * Clones this particle settings.
     *
     * @return the cloned ZParticleDisplay.
     * @see #cloneWithLocation(double, double, double)
     * @see #cloneLocation(double, double, double)
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    @Nonnull
    public ZParticleDisplay clone() {
        ZParticleDisplay display = new ZParticleDisplay(particle, locationCaller, (location == null ? null : cloneLocation(location)), count, offsetx, offsety, offsetz, extra, players);
        if (rotation != null) display.rotation = new Vector(rotation.getX(), rotation.getY(), rotation.getZ());
        display.data = data;
        return display;
    }

    /**
     * Rotates the particle position based on this vector.
     *
     * @param vector the vector to rotate from. The xyz values of this vectors must be radians.
     * @see #rotate(double, double, double)
     * @since 1.0.0
     */
    @Nonnull
    public ZParticleDisplay rotate(@Nonnull Vector vector) {
        Objects.requireNonNull(vector, "Cannot rotate ParticleDisplay with null vector");
        if (rotation == null) rotation = vector;
        else rotation.add(vector);
        return this;
    }

    /**
     * Rotates the particle position based on the xyz radians.
     * Rotations are only supported for some shapes in {@link ZParticle}.
     * Rotating some of them can result in weird shapes.
     *
     * @see #rotate(Vector)
     * @since 3.0.0
     */
    @Nonnull
    public ZParticleDisplay rotate(double x, double y, double z) {
        return rotate(new Vector(x, y, z));
    }


    /**
     * Set the xyz offset of the particle settings.
     *
     * @since 1.1.0
     */
    @Nonnull
    public ZParticleDisplay offset(double x, double y, double z) {
        offsetx = x;
        offsety = y;
        offsetz = z;
        return this;
    }

    /**
     * When a particle is set to be directional it'll only
     * spawn one particle and the xyz offset values are used for
     * the direction of the particle.
     * <p>
     * Colored particles in 1.12 and below don't support this.
     *
     * @return the same particle display.
     * @see #isDirectional()
     * @since 1.1.0
     */
    @Nonnull
    public ZParticleDisplay directional() {
        count = 0;
        return this;
    }

    /**
     * Check if this particle setting is a directional particle.
     *
     * @return true if the particle is directional, otherwise false.
     * @see #directional()
     * @since 2.1.0
     */
    public boolean isDirectional() {
        return count == 0;
    }

    /**
     * Rotates the given xyz with the given rotation radians and
     * adds the to the specified location.
     *
     * @param location the location to add the rotated axis.
     * @param rotation the xyz rotation radians.
     * @return a cloned rotated location.
     * @since 3.0.0
     */
    @Nonnull
    public static Location rotate(@Nonnull Location location, double x, double y, double z, @Nullable Vector rotation) {
        if (rotation == null) return cloneLocation(location).add(x, y, z);

        Vector rotate = new Vector(x, y, z);
        ZParticle.rotateAround(rotate, rotation.getX(), rotation.getY(), rotation.getZ());
        return cloneLocation(location).add(rotate);
    }

    /**
     * Spawns the particle at the current location.
     *
     * @since 2.0.1
     */
    public void spawn() {
        spawn(getLocation(), getPlayers());
    }

    /**
     * Adds xyz of the given vector to the cloned location before
     * spawning particles.
     *
     * @param location the xyz to add.
     * @since 1.0.0
     */
    @Nonnull
    public Location spawn(@Nonnull Vector location, Player... players) {
        Objects.requireNonNull(location, "Cannot add xyz of null vector to ZParticleDisplay");
        return spawn(location.getX(), location.getY(), location.getZ(), players);
    }

    /**
     * Adds xyz to the cloned loaction before spawning particle.
     *
     * @since 1.0.0
     */
    @Nonnull
    public Location spawn(double x, double y, double z, Player... players) {
        return spawn(rotate(getLocation(), x, y, z, rotation), players);
    }

    /**
     * Displays the particle in the specified location.
     * This method does not support rotations if used directly.
     *
     * @param loc the location to display the particle at.
     * @see #spawn(double, double, double, Player...)
     * @since 2.1.0
     */
    @Nonnull
    public Location spawn(@Nonnull Location loc) {
        return spawn(loc, players);
    }

    /**
     * Displays the particle in the specified location.
     * This method does not support rotations if used directly.
     *
     * @param loc     the location to display the particle at.
     * @param players if this particle should only be sent to specific players.
     * @see #spawn(double, double, double, Player...)
     * @since 5.0.0
     */
    @Nonnull
    public Location spawn(@Nonnull Location loc, @Nullable Player... players) {
        if (data != null) {
            if (data instanceof float[]) {
                float[] datas = (float[]) data;
                if (ISFLAT) {
                    Particle.DustOptions dust = new Particle.DustOptions(org.bukkit.Color.fromRGB((int) datas[0], (int) datas[1], (int) datas[2]), datas[3]);
                    if (players == null) {
                        loc.getWorld().spawnParticle(particle, loc, count, offsetx, offsety, offsetz, extra, dust);
                    }
                    else for (Player player : players) {
                        player.spawnParticle(particle, loc, count, offsetx, offsety, offsetz, extra, dust);
                    }
                } else {
                    if (players == null) {
                        loc.getWorld().spawnParticle(particle, loc, count, (int) datas[0], (int) datas[1], (int) datas[2], datas[3]);
                    }
                    else for (Player player : players) {
                        player.spawnParticle(particle, loc, count, (int) datas[0], (int) datas[1], (int) datas[2], datas[3]);
                    }
                }
            }
        } else {
            if (players == null) {
                loc.getWorld().spawnParticle(particle, loc, count, offsetx, offsety, offsetz, extra);
            }
            else for (Player player : players) {
                player.spawnParticle(particle, loc, count, offsetx, offsety, offsetz, extra);
            }
        }
        return loc;
    }
}
