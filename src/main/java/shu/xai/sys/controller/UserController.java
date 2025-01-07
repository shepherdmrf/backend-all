package shu.xai.sys.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import shu.xai.sys.constant.Constants;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.enums.RoleCodeEnums;
import shu.xai.sys.service.LogService;
import shu.xai.sys.service.UserService;
import shu.xai.sys.utils.CoreUtils;
import shu.xai.sys.utils.JSONUtil;
import shu.xai.sys.utils.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;


/**
 * Created by yuziyi on 2021/6/19.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    //开发用户系统通用功能

    @RequestMapping("/login")
    public String login(HttpServletRequest request,HttpServletResponse response){
//        request.getSession().setAttribute();
            //System.out.println(request.getRequestURI());

//        String urlDecoded = URLDecoder.decode(urlEncoded, encoding);
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String loginWay=params.getString("loginWay");
            String loginAccount=params.getString("loginAccount");
            String password=params.getString("password");
            String platformId=params.getString("platformId");

            JSONObject paramsResult=userService.login(loginWay,loginAccount, password,platformId);
            result= paramsResult.toString();
            if(paramsResult.get(Constants.RET_CODE).equals(ResultCodeEnums.SUCCESS.getCode())){
                if(request.getSession(false) != null) {
//                    System.out.println("每次登陆成功改变SessionID！");
                    request.changeSessionId(); //安全考量，每次登录成功改变 Session ID，原理：原来的session注销，拷贝其属性创建新的session对象
                }
                String userId=paramsResult.getString(Constants.RET_DATA);
                request.getSession().setAttribute("userId",userId);
                JSONObject userInfo=userService.getUserInfo(userId);
                request.getSession().setAttribute("roleId",userInfo.get("roleId"));
                request.getSession().setAttribute("platformId",userInfo.get("platformId"));
            }
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }

        return result;
    }

    @RequestMapping("/signOut")
    public String signOut(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            String userId= String.valueOf(request.getSession().getAttribute("userId"));
            request.getSession(false).invalidate();
            logService.logInsert(userId, "/api/user/signOut");
            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),"");
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }

        return result;
    }

    //获取用户可访问目录数据
    @RequestMapping("/getMenuData")
    public String getMenuData(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            String userId= String.valueOf(request.getSession().getAttribute("userId"));
            String roleId= String.valueOf(request.getSession().getAttribute("roleId"));
            JSONArray paramsResult=userService.getMenuData(userId,roleId);
            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    //获取目录数据用于管理
    @RequestMapping("/getMenuManageData")
    public String getMenuManageData(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONArray paramsResult=userService.getMenuManageData();
            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/getOPInfo")
    public String getOPInfo(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONArray paramsResult=userService.getOPInfo();
            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/getUserInfo")
    public String getUserInfo(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            String userId= String.valueOf(request.getSession().getAttribute("userId"));
            JSONObject paramsResult=userService.getUserInfo(userId);
            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/changeUserInfo")
    public String changeUserInfo(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String userInfo=params.getString("userInfo");
            userInfo= URLDecoder.decode(userInfo,Constants.ENCODING_DEF);
            String occupation= URLDecoder.decode(params.getString("occupation"),Constants.ENCODING_DEF);
            String researchDomain= URLDecoder.decode(params.getString("researchDomain"),Constants.ENCODING_DEF);
            String researchDirection= URLDecoder.decode(params.getString("researchDirection"),Constants.ENCODING_DEF);
            String workOrganization= URLDecoder.decode(params.getString("workOrganization"),Constants.ENCODING_DEF);
            String countryNo= params.getString("countryNo");
            JSONArray provinceAndCityNo=  params.getJSONArray("provinceAndCityNo");
            String userId= String.valueOf(request.getSession().getAttribute("userId"));
            JSONObject paramsResult=userService.changeUserInfo(userId,userInfo,occupation,researchDomain,researchDirection,workOrganization,countryNo,provinceAndCityNo);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/resetPassword")
    public String resetPassword(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String newPassword=params.getString("newPassword");
            String userId= String.valueOf(request.getSession().getAttribute("userId"));
            JSONObject paramsResult=userService.resetPassword(userId,newPassword);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/getPlatformInfo")
    public String getPlatformInfo(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONArray paramsResult=userService.getPlatformInfo();
            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    //获取x平台管理员列表
    @RequestMapping("/platformManagers")
    public String platformManagers(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String platformId=params.getString("platformId");
            JSONArray paramsResult=userService.platformManagers(platformId);
            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/changePlatformDetail")
    public String changePlatformDetail(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String platformId=params.getString("platformId");
            String newPlatformDetail=params.getString("newPlatformDetail");
            newPlatformDetail= URLDecoder.decode(newPlatformDetail,Constants.ENCODING_DEF);
            String platformURL=params.getString("platformURL");
            platformURL= URLDecoder.decode(platformURL,Constants.ENCODING_DEF);
            JSONObject paramsResult=userService.changePlatformDetail(newPlatformDetail,platformId,platformURL);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/changePlatformStatus")
    public String changePlatformStatus(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String platformId=params.getString("platformId");
            String activeStatus=params.getString("activeStatus");
            JSONObject paramsResult=userService.changePlatformStatus(platformId,activeStatus);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/addNewPlatform")
    public String addNewPlatform(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String platformName=params.getString("platformName");
            String platformDetail=params.getString("platformDetail");
            platformName= URLDecoder.decode(platformName,Constants.ENCODING_DEF);
            platformDetail= URLDecoder.decode(platformDetail,Constants.ENCODING_DEF);
            String platformAbbr=params.getString("platformAbbr");
            JSONObject paramsResult=userService.addNewPlatform(platformName,platformDetail,platformAbbr);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/addNewOP")
    public String addNewOP(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String opURL=params.getString("opURL");
            String opDescribe=params.getString("opDescribe");
            opURL= URLDecoder.decode(opURL,Constants.ENCODING_DEF);
            opDescribe= URLDecoder.decode(opDescribe,Constants.ENCODING_DEF);
            String opType=params.getString("opType");
            JSONObject paramsResult=userService.addNewOP(opURL,opDescribe,opType);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/addNewMenuMeb")
    public String addNewMenuMeb(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String menuId=params.getString("menuId");
            String name=params.getString("name");
            name= URLDecoder.decode(name,Constants.ENCODING_DEF);
            String path=params.getString("path");
            path= URLDecoder.decode(path,Constants.ENCODING_DEF);
            String fatherId=params.getString("fatherId");
            JSONObject paramsResult=userService.addNewMenuMeb(menuId,name,path,fatherId);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/addNewFMenuMeb")
    public String addNewFMenuMeb(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String menuId=params.getString("menuId");
            String name=params.getString("name");
            name= URLDecoder.decode(name,Constants.ENCODING_DEF);
//            String path=params.getString("path");
//            path= URLDecoder.decode(path,Constants.ENCODING_DEF);
            JSONObject paramsResult=userService.addNewFMenuMeb(menuId,name);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/changeMenuStatus")
    public String changeMenuStatus(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String menuId=params.getString("menuId");
            String menuActive=params.getString("menuActive");
            JSONObject paramsResult=userService.changeMenuStatus(menuId,menuActive);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }


    @RequestMapping("/changeUserStatus")
    public String changeUserStatus(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String userId=params.getString("userId");
            String activeStatus=params.getString("activeStatus");
            JSONObject paramsResult=userService.changeUserStatus(userId,activeStatus);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/addNewManager")
    public String addNewManager(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String userId= CoreUtils.getUUID();
            String userName=params.getString("userName");
            String platformId=params.getString("platformId");
            String platformStatus=params.getString("platformStatus");
            String email=params.getString("email");
            String telephone=params.getString("telephone");
            String loginNo=params.getString("loginNo");
            userName= URLDecoder.decode(userName,Constants.ENCODING_DEF);
            JSONObject paramsResult=userService.addNewManager(userId,userName,platformId,platformStatus,email,telephone,loginNo);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/getOpLogInfo")
    public String getOpLogInfo(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            int offset=params.getInteger("offset");
            int limit=params.getInteger("limit");
            String userId= String.valueOf(request.getSession().getAttribute("userId"));
            String roleId= String.valueOf(request.getSession().getAttribute("roleId"));
            JSONObject paramsResult=logService.getOpLogInfo(userId,roleId,offset,limit);
            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    //获取平台可访问目录数据
    @RequestMapping("/getPlatformMenu")
    public String getPlatformMenu(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String platformId=params.getString("platformId");

            JSONObject paramsResult=new JSONObject();
            JSONArray menuData=userService.getMenuManageData();
            paramsResult.put("menuData",menuData);
            String[] platformMenu=userService.getPlatformMenu(platformId);
            paramsResult.put("platformMenu",platformMenu);

            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/updatePlatformMenu")
    public String updatePlatformMenu(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String platformId=params.getString("platformId");
            JSONArray newMenuList=params.getJSONArray("newMenuList");
            String[] menuList= newMenuList.toArray(new String[0]);
            JSONObject paramsResult=userService.updatePlatformMenu(platformId,menuList);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }


    //平台管理员通用功能
    //
    @RequestMapping("/getUserList")
    public String getUserList(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            String platformId= String.valueOf(request.getSession().getAttribute("platformId"));
            JSONArray paramsResult=userService.getUserList(platformId);
            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/addNewUser")
    public String addNewUser(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String userId=CoreUtils.getUUID();
            String platformId= String.valueOf(request.getSession().getAttribute("platformId"));
            String userName=params.getString("userName");
            userName= URLDecoder.decode(userName,Constants.ENCODING_DEF);
            String email=params.getString("email");
            String telephone=params.getString("telephone");
            String workOrganization= URLDecoder.decode(params.getString("workOrganization"),Constants.ENCODING_DEF);
            String countryNo= params.getString("countryNo");
            JSONArray provinceAndCityNo=  params.getJSONArray("provinceAndCityNo");
            JSONObject paramsResult=userService.addNewUser(userId,userName,platformId,email,telephone,workOrganization,countryNo,provinceAndCityNo);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    //获取user可访问目录数据
    @RequestMapping("/getUserMenu")
    public String getUserMenu(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String userId=params.getString("userId");
            String platformId= String.valueOf(request.getSession().getAttribute("platformId"));


            JSONObject paramsResult=new JSONObject();
            JSONArray platformMenu=userService.getPlatformMenuData(platformId);
            paramsResult.put("platformMenu",platformMenu);
            String[] userMenu=userService.getUserMenu(userId);
            paramsResult.put("userMenu",userMenu);

            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/updateUserMenu")
    public String updateUserMenu(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String userId=params.getString("userId");
            JSONArray newMenuList=params.getJSONArray("newMenuList");
            String[] menuList= newMenuList.toArray(new String[0]);
            JSONObject paramsResult=userService.updateUserMenu(userId,menuList);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    //获取注册user通用可访问目录数据
    @RequestMapping("/getRegisterUserMenu")
    public String getRegisterUserMenu(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String platformId= String.valueOf(request.getSession().getAttribute("platformId"));
            JSONObject paramsResult=new JSONObject();
            JSONArray platformMenu=userService.getPlatformMenuData(platformId);
            paramsResult.put("platformMenu",platformMenu);
            String[] registerUserMenu=userService.getRegisterUserMenu(platformId);
            paramsResult.put("registerUserMenu",registerUserMenu);

            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/updateRegisterUserMenu")
    public String updateRegisterUserMenu(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            JSONArray newMenuList=params.getJSONArray("newMenuList");
            String platformId= String.valueOf(request.getSession().getAttribute("platformId"));
            String[] menuList= newMenuList.toArray(new String[0]);
            JSONObject paramsResult=userService.updateRegisterUserMenu(menuList,platformId);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }


    //普通用户通用功能
    @RequestMapping("/userRegister3")
    public String userRegister3(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            //页面参数
            String userName= URLDecoder.decode(params.getString("userName"),Constants.ENCODING_DEF);
            String email= URLDecoder.decode(params.getString("email"),Constants.ENCODING_DEF);
            String password= params.getString("password");
            String telephone= params.getString("telephone");
            String userInfo= URLDecoder.decode(params.getString("userInfo"),Constants.ENCODING_DEF);
            String occupation= URLDecoder.decode(params.getString("occupation"),Constants.ENCODING_DEF);
            String researchDomain= URLDecoder.decode(params.getString("researchDomain"),Constants.ENCODING_DEF);
            String researchDirection= URLDecoder.decode(params.getString("researchDirection"),Constants.ENCODING_DEF);
            String workOrganization= URLDecoder.decode(params.getString("workOrganization"),Constants.ENCODING_DEF);
            String platformId= params.getString("platformId");
            String countryNo= params.getString("countryNo");
            JSONArray provinceAndCityNo=  params.getJSONArray("provinceAndCityNo");
            JSONObject paramsResult=userService.userRegister3(platformId,userName,email,password,telephone,userInfo,occupation,researchDomain,researchDirection,workOrganization,countryNo,provinceAndCityNo);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }



    //其他
    //分机构唯一的
    @RequestMapping("/valRepeatEmail")
    public String valRepeatEmail(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String email=params.getString("email");
            String roleId=params.getString("userRoleId");
            String platformId="";
            if (RoleCodeEnums.KAIFA.getCode().equals(roleId))
                platformId= params.getString("platformId");
            else if(RoleCodeEnums.IN_MAMAGE.getCode().equals(roleId))
                platformId=String.valueOf(request.getSession().getAttribute("platformId"));
            else
                platformId=params.getString("platformId");

            JSONObject paramsResult=userService.valRepeatEmail(email,platformId);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    //分机构唯一的
    @RequestMapping("/valRepeatTelephone")
    public String valRepeatTelephone(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String telephone=params.getString("telephone");
            String roleId=params.getString("userRoleId");
            String platformId="";
            if (RoleCodeEnums.KAIFA.getCode().equals(roleId))
                platformId= params.getString("platformId");
            else if(RoleCodeEnums.IN_MAMAGE.getCode().equals(roleId))
                platformId=String.valueOf(request.getSession().getAttribute("platformId"));
            else
                platformId=params.getString("platformId");
            JSONObject paramsResult=userService.valRepeatTelephone(telephone,platformId);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/valRepeatLoginNo")
    public String valRepeatLoginNo(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String loginNo=params.getString("loginNo");
            JSONObject paramsResult=userService.valRepeatLoginNo(loginNo);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    //分机构唯一的
    @RequestMapping("/valRepeatUserName")
    public String valRepeatUserName(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String userName=params.getString("userName");
            String roleId=params.getString("userRoleId");
            String platformId="";
            if (RoleCodeEnums.KAIFA.getCode().equals(roleId))
                platformId= params.getString("platformId");
            else if(RoleCodeEnums.IN_MAMAGE.getCode().equals(roleId))
                platformId=String.valueOf(request.getSession().getAttribute("platformId"));
            else
                platformId=params.getString("platformId");
            JSONObject paramsResult=userService.valRepeatUserName(userName,platformId);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/valRepeatMenuId")
    public String valRepeatMenuId(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String menuId=params.getString("menuId");
            JSONObject paramsResult=userService.valRepeatMenuId(menuId);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }
    @RequestMapping("/valRepeatMenuPath")
    public String valRepeatMenuPath(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String menuPath=params.getString("menuPath");
            JSONObject paramsResult=userService.valRepeatMenuPath(menuPath);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    //邮箱验证修改用户状态
    @RequestMapping("/emailRegister/{mass}")
    public String registerActive(@PathVariable("mass")String mass){
        String result="";
        try{
            JSONObject paramsResult=userService.registerActive(mass);
            result=paramsResult.getString(Constants.RET_MESSAGE);
        }catch (Exception e){
            logger.error("error",e);
            result="未知错误";
        }
        return result;
    }

    @RequestMapping("/getSinglePlatformInfo")
    public String getSinglePlatformInfo(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String platformAbbr=params.getString("platformAbbr");
            JSONObject paramsResult=userService.getSinglePlatformInfo(platformAbbr);
            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }


}
