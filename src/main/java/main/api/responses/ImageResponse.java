package main.api.responses;

import lombok.Data;

import java.util.HashMap;

@Data
public class ImageResponse {
    private boolean result;
    private HashMap<String, String> errors;
}
