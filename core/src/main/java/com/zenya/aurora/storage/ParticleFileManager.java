package com.zenya.aurora.storage;

import com.google.gson.Gson;
import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.util.LogUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ParticleFileManager {
    private File PARTICLE_FOLDER = new File(Aurora.getInstance().getDataFolder().getPath() + File.separator + "particles" + File.separator);
    private static ParticleFileManager particleFileManager;
    private HashMap<String, ParticleFile> particleFileMap = new HashMap<>();

    public ParticleFileManager() {
        //Create default particle files if not exists
        if (!PARTICLE_FOLDER.isDirectory()) {
            PARTICLE_FOLDER.mkdirs();
            PARTICLE_FOLDER = new File(Aurora.getInstance().getDataFolder().getPath() + File.separator + "particles" + File.separator);

            try {
                final JarFile jar = new JarFile(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath())); //Read .jar file
                final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
                while (entries.hasMoreElements()) {
                    final String name = entries.nextElement().getName();
                    if (name.startsWith("particles" + File.separator) && name.toLowerCase().endsWith(".json")) { //filter according to the path
                        Files.copy(this.getClass().getClassLoader().getResourceAsStream(name), new File(PARTICLE_FOLDER.getPath(), name.split(File.separator)[name.split(File.separator).length - 1]).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                jar.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Register all files inside a hashmap as ParticleFile classes
        PARTICLE_FOLDER = new File(Aurora.getInstance().getDataFolder().getPath() + File.separator + "particles" + File.separator);
        for (String filename : PARTICLE_FOLDER.list()) {
            if (filename.toLowerCase().endsWith(".json")) registerClass(filename);
        }
    }

    public ParticleFile getClass(String filename) {
        if(!particleFileMap.containsKey(filename) || particleFileMap.get(filename) == null) {
            registerClass(filename);
        }
        return particleFileMap.get(filename);
    }

    public Set<String> getFiles() {
        return particleFileMap.keySet();
    }

    public void registerClass(String filename) {
        Gson gson = new Gson();
        try {
            particleFileMap.put(filename, gson.fromJson(new FileReader(new File(PARTICLE_FOLDER, filename)), ParticleFile.class));
        } catch (Exception e) {
            LogUtils.logError("Error parsing particle file " + filename);
            e.printStackTrace();
            particleFileMap.put(filename, null);
        }
    }

    public void unregisterClass(String name) {
        particleFileMap.remove(name);
    }

    public static void reload() {
        particleFileManager = null;
        getInstance();
    }

    public static ParticleFileManager getInstance() {
        if(particleFileManager == null) {
            particleFileManager = new ParticleFileManager();
        }
        return particleFileManager;
    }
}
