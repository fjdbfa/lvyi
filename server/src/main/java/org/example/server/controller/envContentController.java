package org.example.server.controller;

import org.example.common.result.Result;
import org.example.pojo.dto.PageDto;
import org.example.pojo.envcontent.PageVo;
import org.example.pojo.envcontent.*;
import org.example.server.sever.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/envContent")
public class envContentController  {
    @Autowired
    private EnvContentService envContentService;
    @Autowired
    private EnvContentSpotNewService envContentSpotNewService;
    @Autowired
    private EnvContentMinImgService envContentMinImgService;
    @Autowired
    private EnvContentMaxImgService envContentMaxImgService;
    @Autowired
    private EnvContentSucessStoriesService envContentSucessStoriesService;
    @Autowired
    private EnvContentMessageService envContentMessageService;



    @PostMapping(value="/envContentCarouselData")
    public Result<PageVo<envContentCarouselData>> envContentCarouselData(@RequestBody PageDto envContentCarouselvo) {
        PageVo<envContentCarouselData> carouselData = envContentService.getCarouselData(envContentCarouselvo.getCurrentPage(), envContentCarouselvo.getPageSize());

        return Result.success(carouselData);
    }
    @PostMapping("/xxx")
    public void xxx(){
        System.out.println("1");
        List<envContentMinImgData> page1 = envContentMinImgService.page(1,  6);
        System.out.println(page1);

        System.out.println("123");
    }
    @PostMapping("/envContentSpotNewData")
    public Result envNewData(@RequestBody PageDto envContentCarouselvo) {
        Integer pageNo = envContentCarouselvo.getCurrentPage();
        Integer pageSize = envContentCarouselvo.getPageSize();
        List<envContentMaxImgData> page = envContentMaxImgService.page(pageNo, pageSize);
        System.out.println(page);
        List<envContentMinImgData> page1 = envContentMinImgService.page(pageNo,  pageSize*2);
        return envContentSpotNewService.getSpotNewData(page, page1);
        //return Result.success("wwww000");



  }
  @PostMapping("/envContentSucessStoriesData")
  public Result envContentSucessStoriesData(@RequestBody PageDto envContentCarouselvo) {
      System.out.println("xxxx");
      return envContentSucessStoriesService.getSucessStoriesData(envContentCarouselvo.getCurrentPage(), envContentCarouselvo.getPageSize());

    }
  @PostMapping("/envContentSucessStoriesRandomData")
  public Result envContentSucessStoriesRandomData() {
        return envContentSucessStoriesService.getSucessStoriesRandomData();

  }
  @PostMapping("/detailData")
  public Result getenvContentMessageById(@RequestBody envContentMessageVo envContentMessageVo) {
        String xxx=envContentMessageVo.getType()+envContentMessageVo.getId();
      System.out.println("xxxx"+xxx);
        return envContentMessageService.getMessageById(xxx);
  }

}
