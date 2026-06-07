package org.example.server.sever;

import org.example.common.result.Result;
import org.example.pojo.envcontent.envContentMaxImgData;
import org.example.pojo.envcontent.envContentMinImgData;

import java.util.List;

public interface EnvContentSpotNewService {
    Result getSpotNewData(List<envContentMaxImgData> page, List<envContentMinImgData> page1);
}
