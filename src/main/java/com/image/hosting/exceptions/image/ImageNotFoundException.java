package com.image.hosting.exceptions.image;

public class ImageNotFoundException extends RuntimeException{
    public ImageNotFoundException(String message){
        super(message);
    }
}
