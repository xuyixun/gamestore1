package com.ijianjian.core.domain.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileInfoUpload {
private String filePath;
private String fileName;
private Long fileSize;
}
