package com.zenya.aurora.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.util.Logger;
import com.zenya.aurora.util.RandomNumber;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ParticleFileManager {

    private final Aurora plugin;
    private final File particleFolder;
    private HashMap<String, ParticleFile> particleFileMap = new HashMap<>();
    private Gson gson;

    public ParticleFileManager(Aurora plugin) {
        this.plugin = plugin;
        particleFolder = new File(plugin.getDataFolder(), "particles");

        //Register RandomNumber GSON Object
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(RandomNumber.class, new RandomNumber.RandomNumberDeserializer());
        gson = builder.create();

        try {
            //Create default particle files if not exists
            if (!particleFolder.isDirectory() || !Files.newDirectoryStream(particleFolder.toPath()).iterator().hasNext()) {
                particleFolder.mkdirs();

                try ( //Use ChatBuilder.fileSeparator instead of File.separator to sanitise escape characters in Windows filepaths
                        JarFile jar = new JarFile(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath().
                                replace("%20", " "))) //Read .jar file
                        ) {
                    final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
                    while (entries.hasMoreElements()) {
                        final String path = entries.nextElement().getName();
                        final String name = path.split("/")[path.split("/").length - 1];
                        if (path.contains("particles/") && (name.endsWith(".json") || name.endsWith(".txt"))) {
                            Files.copy(getClass().getClassLoader().getResourceAsStream("particles/" + name), new File(particleFolder,
                                    name).toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                } //gives ALL entries in jar
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        //Register all files inside a hashmap as ParticleFile classes
        for (String filename : particleFolder.list()) {
            if (filename.toLowerCase().endsWith(".json")) {
                registerClass(filename);
            }
        }
    }

    public Set<String> getFileNames() {
        return particleFileMap.keySet();
    }

    public List<ParticleFile> getParticles() {
        List<ParticleFile> classes = new ArrayList<>();
        for (String fileName : getFileNames()) {
            classes.add(particleFileMap.get(fileName));
        }
        return classes;
    }

    public List<String> getParticleNames() {
        List<String> names = new ArrayList<>();
        for (ParticleFile clazz : getParticles()) {
            names.add(clazz.getName().toUpperCase());
        }
        return names;
    }

    public ParticleFile getParticleByName(String particleName) {
        for (ParticleFile particleFile : getParticles()) {
            if (particleFile.getName().toUpperCase().equals(particleName.toUpperCase())) {
                return particleFile;
            }
        }
        return null;
    }

//    private ParticleFile getParticleByFile(String fileName) {
//        if (!particleFileMap.containsKey(fileName) || particleFileMap.get(fileName) == null) {
//            registerClass(fileName);
//        }
//        return particleFileMap.get(fileName);
//    }

    private void registerClass(String filename) {
        try {
            particleFileMap.put(filename, gson.fromJson(new FileReader(new File(particleFolder, filename)), ParticleFile.class));
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
            Logger.logError("Error parsing particle file " + filename);
            e.printStackTrace();
            particleFileMap.put(filename, null);
        }
    }

//    private void unregisterClass(String name) {
//        particleFileMap.remove(name);
//    }

    public void reload() {
        plugin.reloadParticleFileManager();
    }
}
