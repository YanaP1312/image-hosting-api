package com.image.hosting.models.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageTags {
    private List<String> objects;
    private List<String> tags;
    private List<String> colors;
}
