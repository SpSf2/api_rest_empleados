package com.example.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@Component
public class FileDownloadUtil {

    private Path foundFile;

    public Resource getFileAsResource(String fileCode) throws IOException {

        Path dirPath = Paths.get("Files-Upload");

        try {
            foundFile = Files.list(dirPath)
                    .filter(file -> file.getFileName().toString().startsWith(fileCode))
                    .findFirst()
                    .get();
        } catch (IOException ioe) {
            throw new IOException("Error fatal buscando el fichero ", ioe);
        }

        if (foundFile != null) {
            return new UrlResource(foundFile.toUri());
        }

        return null;
    }
}