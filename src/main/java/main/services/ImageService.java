package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.responses.ImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

@Slf4j
@Service
public class ImageService {

    @Value("${api.upload.path}")
    private String uploadPath;

    public String savePhoto(MultipartFile photo, String nameOfElement) {
        String pathToFolder;

        if(nameOfElement.equals("Character")) {
            pathToFolder = "Characters_photos";
        } else if(nameOfElement.equals("Comic")) {
            pathToFolder = "Comics_photos";
        } else {
            pathToFolder = "Other_photos";
        }

        Object pathToPhoto = uploadImageOnServer(photo, pathToFolder);
        if (pathToPhoto.getClass().getName().equals("java.lang.String")) {
            return (String) pathToPhoto;
        } else {
            return null;
        }
    }

    private Object uploadImageOnServer(MultipartFile image, String nameOfFolder) {
        ImageResponse imageResponse = checkUploadingPhoto(image);

        if (imageResponse != null) {
            return new ResponseEntity<>(imageResponse, HttpStatus.OK);
        }

        HashMap<String, String> paths = createPathForPhoto(nameOfFolder);
        String resultFilename = image.getOriginalFilename();

        try {
            if (image.getSize() < 5_242_880) {
                File uploadImage = new File(System.getProperty("user.dir") + File.separator +
                        paths.get("uploadPath") + File.separator + resultFilename);
                image.transferTo(uploadImage);

                if (nameOfFolder.equals("usersPhoto")) {
                    BufferedImage originalImage = ImageIO.read(uploadImage);
                    OutputStream os = new FileOutputStream(uploadImage.getPath(), false);

                    BufferedImage resizedImage = new BufferedImage(36, 36, 5);
                    Graphics2D g = resizedImage.createGraphics();
                    g.drawImage(originalImage, 0, 0, 36, 36, null);
                    g.dispose();

                    ImageIO.write(resizedImage, "jpg", os);
                }

                return paths.get("pathForResponse") + "/" + resultFilename;
            } else {
                imageResponse = new ImageResponse();
                imageResponse.setResult(false);

                HashMap<String, String> errors = new HashMap<>(1);
                errors.put("image", "Размер загружаемого файла больше 5 мб!");

                imageResponse.setErrors(errors);
                return new ResponseEntity<>(imageResponse, HttpStatus.OK);
            }
        } catch (IOException exception) {
            imageResponse = new ImageResponse();
            imageResponse.setResult(false);

            HashMap<String, String> errors = new HashMap<>(1);
            errors.put("image", "Произошла ошибка при загрузке фото на сервер");

            imageResponse.setErrors(errors);
            return new ResponseEntity<>(imageResponse, HttpStatus.OK);
        }
    }

    private ImageResponse checkUploadingPhoto(MultipartFile image) {
        if (image == null ||
                image.getOriginalFilename() == null ||
                image.getOriginalFilename().isEmpty()) {
            ImageResponse imageResponse = new ImageResponse();
            imageResponse.setResult(false);

            HashMap<String, String> errors = new HashMap<>(1);
            errors.put("image", "Файл загружен с ошибкой");

            imageResponse.setErrors(errors);
            return imageResponse;
        } else {
            return null;
        }
    }

    private HashMap<String, String> createPathForPhoto(String nameOfFolder) {
        HashMap<String, String> paths = new HashMap<>();
        String imagesPath = uploadPath + File.separator + nameOfFolder;
        File uploadCatalog = new File(imagesPath);

        if (!uploadCatalog.exists()) {
            uploadCatalog.mkdirs();
        }

        String uploadPath = imagesPath;
        String pathForResponse = "/upload/" + nameOfFolder;
        String newUploadPath;
        for (int i = 0; i < 3; i++) {
            String alphaNumericString = getAlphaNumericString(2);
            newUploadPath = uploadPath + File.separator + alphaNumericString;
            pathForResponse = pathForResponse + "/" + alphaNumericString;
            File uploadDir = new File(newUploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            uploadPath = newUploadPath;
        }
        paths.put("uploadPath", uploadPath);
        paths.put("pathForResponse", pathForResponse);
        return paths;
    }

    private String getAlphaNumericString(int n) {
        // выбрал символ случайный из этой строки

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

                + "0123456789"

                + "abcdefghijklmnopqrstuvxyz";

        // создаем StringBuffer размером AlphaNumericString

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            // генерируем случайное число между

            // 0 переменной длины AlphaNumericString

            int index

                    = (int) (AlphaNumericString.length()

                    * Math.random());

            // добавляем символ один за другим в конец sb

            sb.append(AlphaNumericString

                    .charAt(index));

        }

        return sb.toString();

    }

    public boolean deleteUserPhotoFromServer(String pathToPhoto) {
        if (pathToPhoto == null) {
            return false;
        }


        File file = new File(System.getProperty("user.dir") + File.separator + pathToPhoto);
        if (file.exists()) {
            return file.delete();
        } else {
            return false;
        }

    }
}
