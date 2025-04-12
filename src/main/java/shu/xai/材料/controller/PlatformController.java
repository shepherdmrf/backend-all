package shu.xai.材料.controller;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shu.xai.sys.controller.UserController;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.utils.ResultUtils;
import shu.xai.材料.page.PageRequest;
import shu.xai.材料.page.value;
import shu.xai.材料.page.PageRequestCopy;
import shu.xai.材料.service.PlatformService;
import shu.xai.材料.page.Knowledge;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static sun.misc.Version.print;

@RestController
@RequestMapping("/user")
public class PlatformController {
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private PlatformService platformService;

    @RequestMapping("/KNNAnalyze")
    public String KNNAnalyze(HttpServletRequest request) {
        String result = "";
        try {
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            JSONObject nodes=platformService.Analyrizeknn(userId,roleId);
            platformService.ServerExcelTable(nodes);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(),nodes.toJSONString());
        } catch (Exception e) {
            logger.error("error", e);
            result = ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "");
        }
        return result;
    }

    @RequestMapping("/getSign")
    public String getSignTable(HttpServletRequest request) {
        String result = "";
        try {
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            JSONObject paramsResult = platformService.GetSign(userId, roleId);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(), paramsResult.toJSONString());
        } catch (Exception e) {
            logger.error("error", e);
            result = ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "");
        }
        return result;
    }

    @RequestMapping("/FeatureTable")
    public String FeatureTable(@RequestBody PageRequestCopy pageRequest, HttpServletRequest request) {
        String result = "";
        try {
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            int page = pageRequest.getPage();
            int pagesize = pageRequest.getPageSize();
            int repeat = pageRequest.getRepeat();
            int iteration = pageRequest.getIteration();
            String value1 = pageRequest.getValue1();
            String value=pageRequest.getValue();
            Integer radio=pageRequest.getRadio();
            platformService.SolveFeatureTable(userId, roleId, repeat, iteration, value,radio);
            JSONObject paramsResult = platformService.SearchFeatureTable(userId, roleId, page, pagesize, value1);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(), paramsResult.toJSONString());
        } catch (Exception e) {
            logger.error("error", e);
            result = ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "");
        }
        return result;
    }

    @RequestMapping("/searchFeatureTable")
    public String searchFeature(@RequestBody PageRequestCopy pageRequestCopy, HttpServletRequest request) {
        String result = "";
        try {
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            int page = pageRequestCopy.getPage();
            int pageSize = pageRequestCopy.getPageSize();
            String value1 = pageRequestCopy.getValue1();
            JSONObject paramsResult = platformService.SearchFeatureTable(userId, roleId, page, pageSize, value1);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(), paramsResult.toJSONString());
        } catch (Exception e) {
            logger.error("error", e);
            result = ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "");
        }
        return result;
    }


    @RequestMapping("/training")
    public String training(@RequestBody Knowledge tp, HttpServletRequest request) {
        String result = "";
        try {
            System.out.println("Received KnowledgeList: " + tp.getV());
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            List<List<Integer>> KnowledgeKernels = tp.getV();
            JSONObject paramsResult = platformService.GetKnowlegekernels(userId, roleId, KnowledgeKernels);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(), paramsResult.toJSONString());
        } catch (Exception e) {
            logger.error("error", e);
            result = ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "");
        }
        return result;
    }

    @RequestMapping("/cluster")
    public String cluster(@RequestBody PageRequest pageRequest, HttpServletRequest request) {
        String result = "";
        try {
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            int page = pageRequest.getPage();
            int pagesize = pageRequest.getPageSize();
            String value1 = pageRequest.getValue1();
            platformService.Solvecluster();
            JSONObject paramsResult = platformService.SearchFeatureTable(userId, roleId, page, pagesize, value1);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(), paramsResult.toJSONString());
        } catch (Exception e) {
            logger.error("error", e);
            result = ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "");
        }
        return result;
    }

    @RequestMapping("/simple")
    public String Solvesimple(@RequestBody value value, HttpServletRequest request) {
        String result = "";
        try {
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            platformService.SolvesimpleDecision(userId, roleId);
            String v = value.getValue();
            JSONObject paramsResult = platformService.Search(userId, roleId, v);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(), paramsResult.toJSONString());
        } catch (Exception e) {
            logger.error("error", e);
            result = ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "");
        }
        return result;
    }

    @RequestMapping("/searchsimple")
    public String Searchsimple(@RequestBody value value, HttpServletRequest request) {
        String result = "";
        try {
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            String v = value.getValue();
            JSONObject paramsResult = platformService.Search(userId, roleId, v);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(), paramsResult.toJSONString());
        } catch (Exception e) {
            logger.error("error", e);
            result = ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "");
        }
        return result;
    }

    @RequestMapping("/kernel")
    public String solvekernel(@RequestBody value value, HttpServletRequest request) {
        String result = "";
        try {
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            platformService.Solvekernel(userId, roleId);
            String v = value.getValue();
            JSONObject paramsResult = platformService.Search(userId, roleId, v);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(), paramsResult.toJSONString());
        } catch (Exception e) {
            logger.error("error", e);
            result = ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "");
        }
        return result;
    }

    @RequestMapping("/searchkernel")
    public String Searchkernel(@RequestBody value value, HttpServletRequest request) {
        String result = "";
        try {
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            String v = value.getValue();
            JSONObject paramsResult = platformService.Search(userId, roleId, v);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(), paramsResult.toJSONString());
        } catch (Exception e) {
            logger.error("error", e);
            result = ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "");
        }
        return result;
    }


    @RequestMapping("/positivekernel")
    public String solvepositivekernel(@RequestBody value value, HttpServletRequest request) {
        String result = "";
        try {
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            platformService.SolvePositiveKernel(userId, roleId);
            String v = value.getValue();
            JSONObject paramsResult = platformService.Search(userId, roleId, v);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(), paramsResult.toJSONString());
        } catch (Exception e) {
            logger.error("error", e);
            result = ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "");
        }
        return result;
    }

    @RequestMapping("/searchpositivekernel")
    public String Searchpositivekernel(@RequestBody value value, HttpServletRequest request) {
        String result = "";
        try {
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            String v = value.getValue();
            JSONObject paramsResult = platformService.Search(userId, roleId, v);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(), paramsResult.toJSONString());
        } catch (Exception e) {
            logger.error("error", e);
            result = ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "");
        }
        return result;
    }
    @RequestMapping("/getMlrPicture")
    public String MlrPicture(HttpServletRequest request) {
        String result="";
        try{
            String userId= String.valueOf(request.getSession().getAttribute("userId"));
            String roleId= String.valueOf(request.getSession().getAttribute("roleId"));
            JSONObject paramsResult=platformService.DrawMlrPicture(userId,roleId);
            System.out.println(paramsResult);
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }
    @RequestMapping("/getKnnPicture")
    public String KnnPicture(HttpServletRequest request) {
        String result="";
        try{
            String userId= String.valueOf(request.getSession().getAttribute("userId"));
            String roleId= String.valueOf(request.getSession().getAttribute("roleId"));
            JSONObject paramsResult=platformService.DrawKnnPicture(userId,roleId);
            System.out.println(paramsResult);
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/MLRAnalyze")
    public String MLRAnalyze(HttpServletRequest request) {
        String result="";
        try{
            String userId= String.valueOf(request.getSession().getAttribute("userId"));
            String roleId= String.valueOf(request.getSession().getAttribute("roleId"));
            JSONObject paramsResult=platformService.MLRRelationAnalyze(userId,roleId);
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }
}





