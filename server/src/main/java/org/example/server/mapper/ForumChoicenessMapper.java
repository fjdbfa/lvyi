package org.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.ForumChoiceness;
import org.example.pojo.envcontent.envContentCarouselData;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumChoicenessMapper extends BaseMapper<ForumChoiceness> {

}
