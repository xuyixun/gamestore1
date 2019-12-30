package com.ijianjian.file.domain.po;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.ijianjian.core.domain.user.po.CoreUser;
import com.ijianjian.file.util.FieldConstant.FileType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_file_info")
@Builder
@Getter
@Setter
public class FileInfo {
@Tolerate
FileInfo() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime fCreateTime;
@UpdateTimestamp
private LocalDateTime fUpdateTime;
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn
private CoreUser createUser;

private boolean fDeleted;
private LocalDateTime fDeletedTime;

private String fFileName;
private String fFileNameOriginal;
private long fSize;
private String fPath;
private String fContentType;
private String fExt;
private String fServer;
private String fClient;
@Enumerated(EnumType.STRING)
private FileType type;
}
