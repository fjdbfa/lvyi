package org.example.pojo.envcontent;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@TableName(value = "env_content_success_stories_data", autoResultMap = true)
public class envContentSucessStoriesData {
  private Integer id;
  private String address;
  private String title;
  private String imageUrl;
  private Date date;
}
