package com.atatame.medicineassistantsystem.mapper;

import com.atatame.medicineassistantsystem.model.entity.ProjectDocument;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProjectDocumentMapper extends BaseMapper<ProjectDocument> {

    @Select("SELECT * FROM project_document WHERE project_id = #{projectId} AND (doc_name LIKE CONCAT('%',#{keyword},'%') OR IFNULL(summary,'') LIKE CONCAT('%',#{keyword},'%')) ORDER BY upload_time DESC")
    List<ProjectDocument> searchKeyword(@Param("projectId") Long projectId, @Param("keyword") String keyword);
}
