package com.example.demo.controllers;


import com.example.demo.exceptions.StorageFileNotFoundException;
import com.example.demo.jython.PythonService;
import com.example.demo.jython.PythonServiceFactory;
import com.example.demo.services.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDate;
import java.util.stream.Collectors;


@Controller
public class FileUploadController {
    private final Instant sessionStartTime = Instant.now();
    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired
    private PythonServiceFactory pythonServiceFactory;

    @GetMapping("/")
    public String listUploadedFiles(Model model) {
        model.addAttribute("files", storageService.loadAll().map(
                path -> {
                    try {
                        FileTime time = Files.getLastModifiedTime(path);
                        String builder = "";
                        BufferedImage bimg = ImageIO.read(new File(path.toString()));
                        int width = bimg.getWidth();
                        int height  = bimg.getHeight();
                        if (time.toInstant().isAfter(sessionStartTime)) {
                            builder = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                    "serveFile", path.getFileName().toString()).build().toString()
                                    + "; " + FilenameUtils.getBaseName(path.toString())
                                    + "; " + Files.size(path)
                                    + "; " + width + " x " + height
                                    + "; " + FilenameUtils.getExtension(path.toString()
                                    + "; " + pythonServiceFactory.getRating(path));
                        }
                        return builder;

                    } catch (IOException e) {
                        //throw new StorageFileNotFoundException("File not found. " + e.getMessage());
                        System.out.println("issue with: " + path + ": " + e.getLocalizedMessage());
                        return "";
                    }
                }
        ).filter(value -> !value.isEmpty()).collect(Collectors.toList()));
        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("urlLink") String urlLink,
                                   RedirectAttributes redirectAttributes) {
        if (!urlLink.isEmpty()) {
            storageService.getfromURL(urlLink);
        }
        else {
            storageService.store(file);
        }

        redirectAttributes.addFlashAttribute("message",
                "Upload successful. Uploaded: " + file.getOriginalFilename());

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
