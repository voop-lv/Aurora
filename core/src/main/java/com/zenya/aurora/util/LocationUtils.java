package com.zenya.aurora.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;


public final class LocationUtils {
    /**
     *
     * @param loc Location to check
     * @return The centre of the block at a location
     */
    public static Location getBlockCentre(Location loc) {
        return new Location(loc.getWorld(), (int) loc.getX() + 0.5, (int) loc.getY() + 0.5, (int) loc.getZ() + 0.5);
    }

    /**
     *
     * @param block Block location to check
     * @return The centre of the block at a location
     */
    public static Location getBlockCentre(Block block) {
        return new Location(block.getWorld(), block.getLocation().getX()+0.5, block.getLocation().getY()+0.5, block.getLocation().getZ()+0.5);
    }

    /**
     *
     * @param coord Location coordinate (double) to convert
     * @return An int chunk coordinate
     */
    public static int getChunkCoord(Double coord) {
        return (int) Math.floor(coord) >> 4;
    }

    /**
     *
     * @param coord1 x location coordinate (double) to convert
     * @param coord1 z location coordinate (double) to convert
     * @return An array of int chunk coordinates (x, z)
     */
    public static int[] getChunkCoords(Double coord1, Double coord2) {
        return new int[]{getChunkCoord(coord1), getChunkCoord(coord2)};
    }

    /**
     *
     * @param chunk The centre reference chunk
     * @param radius Square radius to add around centre chunk
     * @return An array of nearby chunks within the radius
     */
    public static Chunk[] getSurroundingChunks(Chunk chunk, int radius) {
        ArrayList<Chunk> nearbyChunks = new ArrayList<Chunk>();
        int cX = chunk.getX();
        int cZ = chunk.getZ();

        for(int x=cX-radius; x<=cX+radius; x++) {
            for(int z=cZ-radius; z<=cZ+radius; z++) {
                nearbyChunks.add(chunk.getWorld().getChunkAt(x, z));
            }
        }
        return nearbyChunks.toArray(new Chunk[nearbyChunks.size()]);
    }

    /**
     *
     * @param nearbyChunks An array of chunks to check
     * @return An array containing 2 location bounds
     */
    private static Location[] getLocationBounds(Chunk[] nearbyChunks) {
        //Get chunk bounds
        Chunk c1 = nearbyChunks[0];
        Chunk c2 = nearbyChunks[0];
        for(Chunk c : nearbyChunks) {
            if(c.getX() < c1.getX() && c.getZ() < c1.getZ()) c1 = c; //Lower chunk bound
            if(c.getX() > c1.getX() && c.getZ() > c1.getZ()) c2 = c; //Upper chunk bound
        }

        //Get location bounds
        Location lowerbound = c1.getBlock(0, 0, 0).getLocation();
        Location upperbound = c2.getBlock(15, 255, 15).getLocation();

        //Redundant because lower and upper chunk bounds are already defined
        /*
        Chunk lowerX = c1;
        Chunk higherX = c2;
        Chunk lowerZ = c1;
        Chunk higherZ = c2;
        int c1x = c1.getX();
        int c2x = c2.getX();
        int c1z = c1.getZ();
        int c2z = c2.getZ();

        //Set higher/lower x and z for chunks
        if(c1x > c2x) {
            higherX = c1;
            lowerX = c2;
        }
        if(c1z > c2z) {
            higherZ = c1;
            lowerZ = c2;
        }

        if(lowerX.equals(lowerZ)) {
            //Same chunk has lowest x and z
            lowerbound = lowerX.getBlock(0, y1, 0).getLocation();
            upperbound = higherX.getBlock(15, y2, 15).getLocation();
        } else {
            //Different chunks have lowest x and z
            lowerbound = lowerX.getBlock(0, y1, 15).getLocation();
            upperbound = higherX.getBlock(15,y2, 0).getLocation();
        }
        */

        return new Location[]{lowerbound, upperbound};
    }

    /**
     *
     * @param chunks An array of chunks to check
     * @return An array of available locations to spawn particles
     */
    public static CompletableFuture<Location[]> getParticleLocations(Chunk[] chunks) {
        return getParticleLocations(chunks, 80, 100);
    }

    /**
     *
     * @param chunks An array of chunks to check
     * @param y1 First y-bound
     * @param y2 Second y-bound
     * @return An array of available locations to spawn particles
     */
    public static CompletableFuture<Location[]> getParticleLocations(Chunk[] chunks, int y1, int y2) {
        return getParticleLocations(chunks, y1, y2, 20, 0.5f, true);
    }

    /**
     *
     * @param chunks An array of chunks to check
     * @param y1 First y-bound
     * @param y2 Second y-bound
     * @param distance Minimum distance between each particle group
     * @param randMultiplier Extent of randomness in distance between particle groups
     * @param shuffle Whether returned locations should be randomised
     * @return An array of available locations to spawn particles
     */
    public static CompletableFuture<Location[]> getParticleLocations(Chunk[] chunks, int y1, int y2, double distance, float randMultiplier, boolean shuffle) {
        CompletableFuture<Location[]> future = CompletableFuture.supplyAsync(() -> {
            //Init variables
            Random randObj = ThreadLocalRandom.current();

            //Get location bounds
            Location[] locBounds = getLocationBounds(chunks);
            Location l1 = locBounds[0];
            Location l2 = locBounds[1];
            double lowerX = l1.getX();
            double higherX = l2.getX();
            double lowerY = Math.min(y1, y2);
            double higherY = Math.max(y1, y2);
            double lowerZ = l1.getZ();
            double higherZ = l2.getZ();

            //Redundant because lower and upper chunk bounds are already defined
            /*
            double lowerX = l1.getX();
            double higherX = l2.getX();
            double lowerY = l1.getY();
            double higherY = l2.getY();
            double lowerZ = l1.getZ();
            double higherZ = l2.getZ();
            if(l1.getX() > l2.getX()) {
                higherX = l1.getX();
                lowerX = l2.getX();
            }
            if(l1.getY() > l2.getY()) {
                higherY = l1.getY();
                lowerY = l2.getY();
            }
            if(l1.getZ() > l2.getZ()) {
                higherZ = l1.getZ();
                lowerZ = l2.getZ();
            }
            */

            //Add to list of available locations
            ArrayList<Location> locList = new ArrayList<>();
            for(double x=lowerX; x<higherX; x+=distance) {
                for(double z=lowerZ; z<higherZ; z+=distance) {
                    double locY = randObj.nextInt((int) (higherY-lowerY)) + (int) lowerY + 1 + randObj.nextFloat();
                    double locX = x + randObj.nextFloat()*randMultiplier*distance;
                    double locZ = z + randObj.nextFloat()*randMultiplier*distance;
                    locList.add(new Location(l1.getWorld(), locX, locY, locZ));
                }
            }

            Location[] locs = locList.toArray(new Location[locList.size()]);
            //Randomise locations
            if(shuffle) {
                int n = locs.length;
                for (int i = 0; i < n; i++) {
                    int randomValue = i + randObj.nextInt(n - i);
                    Location randomElement = locs[randomValue];
                    locs[randomValue] = locs[i];
                    locs[i] = randomElement;
                }
            }
            return locs;
        });
        return future;
    }
}
