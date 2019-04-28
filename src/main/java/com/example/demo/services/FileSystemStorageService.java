package com.example.demo.services;

import com.example.demo.StorageProperties;
import com.example.demo.exceptions.StorageException;
import com.example.demo.exceptions.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;



@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException ioe) {
            throw new StorageException("Could not initialize storage", ioe);
        }
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Cannot store empty file: " + file.getOriginalFilename());
            }
            Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
        } catch (IOException ioe) {
            throw new StorageException("Failed to store file: " + file.getOriginalFilename(), ioe);
        }
    }

    // For handling URLs - https://www.baeldung.com/java-download-file
    // did not finish
    @Override
    public void getfromURL(String input_url) {
        try {
            URL url = new URL(input_url);
            String urlPath = url.getPath();
            String fileName = urlPath.substring(urlPath.lastIndexOf('/') + 1);


            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());

            FileOutputStream fileOutputStream = new FileOutputStream(this.rootLocation.resolve(fileName).toString());
            FileChannel fileChannel = fileOutputStream.getChannel();

            fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileChannel.close();
            fileOutputStream.close();
        } catch (Exception e) {
            throw new StorageFileNotFoundException("Url is invalid: " + input_url, e);
        }
        //MultipartFile file
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1);
                    //.filter(path -> !path.equals(this.rootLocation))
                    //.map(path -> this.rootLocation.relativize(path));
        } catch (IOException ioe) {throw new StorageException("Failed to read stored files"); }
    }

    @Override
    public Path load(String filename) {
        return this.rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {throw new StorageFileNotFoundException("Could not read file: " + filename, e);}
    }


    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
