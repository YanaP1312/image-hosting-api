package com.image.hosting.models.helpers;

import io.swagger.v3.oas.annotations.media.Schema;
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
  @Schema(example = "[\"car\", \"tree\", \"cloud\"]")
  private List<String> objects;
  @Schema(example = "[\"sunset\", \"outdoor\"]")
  private List<String> tags;
  @Schema(example = "[\"blue\", \"orange\"]")
  private List<String> colors;
}
