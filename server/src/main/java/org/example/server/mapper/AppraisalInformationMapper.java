package org.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.pojo.entity.AppraisalInformation;
import org.example.pojo.entity.ForumChoiceness;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Mapper
public interface AppraisalInformationMapper  extends BaseMapper<AppraisalInformation> {

    @Insert("INSERT INTO appraisal_information (json_data, description) VALUES (#{jsonData}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(AppraisalInformation entity);

    @Select("SELECT * FROM appraisal_information WHERE id = #{id}")
    AppraisalInformation selectById(Long id);
}
