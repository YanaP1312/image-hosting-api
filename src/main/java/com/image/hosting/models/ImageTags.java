package com.image.hosting.models;

import lombok.*;

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
