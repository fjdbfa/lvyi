package org.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.Emp;
import org.springframework.stereotype.Repository;


@Repository
public interface userMapper extends BaseMapper<Emp> {
}
