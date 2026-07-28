package com.image.hosting.exceptions.image;

public class ImageTooLargeException extends RuntimeException {
    public ImageTooLargeException(String message){
        super(message);
    }
}
