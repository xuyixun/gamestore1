package com.ijianjian.file.domain.repository;

import org.springframework.data.jpa.repository.Query;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.file.domain.po.FileInfo;

public interface FileInfoRepository extends IRepository<FileInfo, String> {

@Query(value = "select f_file_name_original from t_file_info where  concat(f_path,'/',f_file_name)=?1", nativeQuery = true)
String findName(String path);
}
