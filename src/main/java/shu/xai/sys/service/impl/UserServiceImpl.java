package shu.xai.sys.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import shu.xai.sys.component.EmailUtils;
import shu.xai.sys.enums.LoginWayCodeEnums;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.enums.RoleCodeEnums;
import shu.xai.sys.enums.StatusCodeEnums;
import shu.xai.sys.service.UserService;
import shu.xai.sys.utils.CoreUtils;
import shu.xai.sys.utils.ResultUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuziyi on 2021/6/19.
 */
@Service("UserService")
@Transactional(transactionManager ="TransactionManagerXAI")
public class UserServiceImpl implements UserService{

    @Resource(name = "jdbcTemplateXAI")
    private JdbcTemplate jdbcTemplateXAI;

    @Resource
    EmailUtils emailUtils;

    //开发人员系统操作


    @Override
    public JSONObject login(String loginWay, String loginAccount,String password,String platformId) {
        JSONObject result=new JSONObject();
        //正则校验
        String regexPassword = "[0-9a-zA-Z]{32}";
        List<Map<String,Object>> searchResult;
        if(LoginWayCodeEnums.LOGIN_EMAIL.getCode().equals(loginWay)){
            String regexEmail = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            if(!loginAccount.matches(regexEmail)||!password.matches(regexPassword)){
                return ResultUtils.commonResultJSON(ResultCodeEnums.ERR_STYLE.getCode(),ResultCodeEnums.ERR_STYLE.getMsg());
            }
            searchResult=jdbcTemplateXAI.queryForList("select t.password,t.user_id from sys_user_info t where t.email=? and t.user_active=? and t.user_platform_id=?",
                    loginAccount, StatusCodeEnums.ACTIVE.getCode(),platformId);
        }else if(LoginWayCodeEnums.LOGIN_TELE.getCode().equals(loginWay)){
            String regexTelephoneNo = "^1[345678]\\d{9}$";
            if(!loginAccount.matches(regexTelephoneNo)||!password.matches(regexPassword)){
                return ResultUtils.commonResultJSON(ResultCodeEnums.ERR_STYLE.getCode(),ResultCodeEnums.ERR_STYLE.getMsg());
            }
            searchResult=jdbcTemplateXAI.queryForList("select t.password,t.user_id  from sys_user_info t where t.telephone=? and t.user_active=? and t.user_platform_id=?",
                    loginAccount, StatusCodeEnums.ACTIVE.getCode(),platformId);
        }else {
            String regexLoginNo = "[0-9]{1,10}";
            if(!loginAccount.matches(regexLoginNo)||!password.matches(regexPassword)){
                return ResultUtils.commonResultJSON(ResultCodeEnums.ERR_STYLE.getCode(),ResultCodeEnums.ERR_STYLE.getMsg());
            }
            searchResult=jdbcTemplateXAI.queryForList("select t.password,t.user_id from sys_user_info t where t.login_no=? and t.user_active=? and t.user_platform_id=?",
                    loginAccount, StatusCodeEnums.ACTIVE.getCode(),platformId);
        }

        if(searchResult.size()<1){
            result= ResultUtils.commonResultJSON(ResultCodeEnums.NO_USER.getCode(),ResultCodeEnums.NO_USER.getMsg());
        }else{
            if(searchResult.get(0).get("password").equals(password)){
                result=ResultUtils.commonJSONSuccess(String.valueOf(searchResult.get(0).get("user_id")));
            }else{
                result= ResultUtils.commonResultJSON(ResultCodeEnums.ERR_PASSWORD.getCode(),ResultCodeEnums.ERR_PASSWORD.getMsg());
            }
        }
        return result;
    }

    @Override
    public JSONArray getMenuData(String userId,String roleId) {
        JSONArray result=new JSONArray();
        List<Map<String, Object>> searchResult;
        //菜单查询
        if(roleId.equals(RoleCodeEnums.KAIFA.getCode())||roleId.equals(RoleCodeEnums.IN_MAMAGE.getCode())) {
            //按角色菜单对照表
            searchResult = jdbcTemplateXAI.queryForList(
                    "select t1.menu_id,t1.menu_father_id,t1.menu_level,t1.menu_name,t1.menu_path from sys_menu_info t1 \n" +
                            "LEFT JOIN sys_role_menu t2 on t1.menu_id=t2.menu_id  \n" +
                            "LEFT JOIN sys_user_info t3 on t2.role_id=t3.role_id and t2.role_platform_id=t3.user_platform_id \n" +
                            "WHERE t3.user_id=? and t1.menu_active=?",
                    userId, StatusCodeEnums.ACTIVE.getCode());
        }else {
            //按用户菜单对照表 // 待优化 表顺序换下，先不改了,数据不多无所谓
            searchResult = jdbcTemplateXAI.queryForList(
                    "select t1.menu_id,t1.menu_father_id,t1.menu_level,t1.menu_name,t1.menu_path from sys_menu_info t1 \n" +
                            "LEFT JOIN sys_puser_menu t2 on t1.menu_id=t2.menu_id \n" +
                            "WHERE t2.user_id=? and t1.menu_active=?",
                    userId, StatusCodeEnums.ACTIVE.getCode());
        }
        //二级树结构生成
        result= CoreUtils.getMenuTree(searchResult);
        return result;
    }

    @Override
    public JSONArray getMenuManageData() {
        JSONArray result=new JSONArray();
        //菜单查询
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("select t1.menu_id,t1.menu_father_id,t1.menu_level,t1.menu_name,t1.menu_path,t1.menu_active from sys_menu_info t1");
        //二级树结构生成
        result= CoreUtils.getMenuTree(searchResult);
        return result;
    }

    @Override
    public JSONArray getOPInfo() {
        JSONArray result=new JSONArray();
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.op_url,t.op_type,t.op_describe FROM sys_op_info t");
        for(Map item:searchResult){
            JSONObject re=new JSONObject();
            re.put("opURL",item.get("op_url"));
            re.put("opType",item.get("op_type"));
            re.put("opDescribe",item.get("op_describe"));
            result.add(re);
        }
        return result;
    }

    @Override
    public JSONObject getUserInfo(String userId) {
        JSONObject result=new JSONObject();
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("select t1.user_id,t1.user_name,t1.email,t1.telephone,t1.login_no,t1.role_id,t2.role_name,t1.user_info,t1.user_platform_id,t3.platform_name,t1.occupation,t1.research_domain,t1.research_direction,t1.work_organization,t1.country_id,t1.province_id,t1.city_id from sys_user_info t1\n" +
                        "LEFT JOIN sys_role_info t2 on t1.role_id=t2.role_id\n" +
                        "LEFT JOIN sys_platform_info t3 on t3.platform_id=t1.user_platform_id\n" +
                        "where t1.user_id=?",
                userId);
        result.put("userId",searchResult.get(0).get("user_id"));
        result.put("userName",searchResult.get(0).get("user_name"));
        result.put("email",searchResult.get(0).get("email"));
        result.put("telephone",searchResult.get(0).get("telephone"));
        result.put("loginNo",searchResult.get(0).get("login_no"));
        result.put("roleId",searchResult.get(0).get("role_id"));
        result.put("userInfo",searchResult.get(0).get("user_info"));
        result.put("role",searchResult.get(0).get("role_name"));
        result.put("platformId",searchResult.get(0).get("user_platform_id"));
        result.put("platform",searchResult.get(0).get("platform_name"));
        result.put("occupation",searchResult.get(0).get("occupation"));
        result.put("researchDomain",searchResult.get(0).get("research_domain"));
        result.put("researchDirection",searchResult.get(0).get("research_direction"));
        result.put("workOrganization",searchResult.get(0).get("work_organization"));
        result.put("countryNo",searchResult.get(0).get("country_id"));
        result.put("provinceNo",searchResult.get(0).get("province_id"));
        result.put("cityNo",searchResult.get(0).get("city_id"));

        return result;
    }

    @Override
    public JSONObject changeUserInfo(String userId, String userInfo,String occupation,String researchDomain,String researchDirection,String workOrganization,String countryNo,JSONArray provinceAndCityNo) {
        JSONObject result=new JSONObject();
        if(provinceAndCityNo.size()==0) {
            jdbcTemplateXAI.update("UPDATE sys_user_info t set t.user_info=?,t.occupation=?,t.research_domain=?,t.research_direction=?,t.work_organization=?,t.country_id=? where t.user_id=?",
                    userInfo, occupation, researchDomain, researchDirection, workOrganization, countryNo, userId);
        }else {
            String provinceNo = String.valueOf(provinceAndCityNo.get(0));
            String cityNo = String.valueOf(provinceAndCityNo.get(1));
            jdbcTemplateXAI.update("UPDATE sys_user_info t set t.user_info=?,t.occupation=?,t.research_domain=?,t.research_direction=?,t.work_organization=?,t.country_id=?,t.province_id=?,t.city_id=? where t.user_id=?",
                    userInfo, occupation, researchDomain, researchDirection, workOrganization, countryNo,provinceNo,cityNo, userId);


        }
        result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg());

        return  result;
    }

    public JSONObject resetPassword(String userId, String newPassword) {
        JSONObject result=new JSONObject();
        int resultRow=jdbcTemplateXAI.update("UPDATE sys_user_info t set t.password=? where t.user_id=?",
                newPassword,userId);
        if(resultRow==1){
            result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg());
        }else{
            result= ResultUtils.commonResultJSON(ResultCodeEnums.UPDATE_ERROR.getCode(),ResultCodeEnums.UPDATE_ERROR.getMsg());
        }
        return  result;
    }

    @Override
    public JSONArray getPlatformInfo() {
        JSONArray result=new JSONArray();
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.platform_id,t.platform_name,t.platform_detail,t.platform_abbr,t.platform_active,t.platform_url FROM sys_platform_info t");
        for(Map item:searchResult){
            JSONObject re=new JSONObject();
            re.put("platformId",item.get("platform_id"));
            re.put("platformName",item.get("platform_name"));
            re.put("platformDetail",item.get("platform_detail"));
            re.put("platformAbbr",item.get("platform_abbr"));
            re.put("inActive",item.get("platform_active"));
            re.put("platformURL",item.get("platform_url"));

//            re.put("platformActive",StatusCodeEnums.ACTIVE.getCode().equals(String.valueOf(item.get("platform_active")))?"已激活":"禁用");
            result.add(re);
        }
        return result;
    }

    @Override
    public JSONArray platformManagers(String platformId) {
        JSONArray result=new JSONArray();
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.user_id,t.user_name,t.email,t.telephone,t.login_no,t.user_active FROM sys_user_info t WHERE t.role_id=? and t.user_platform_id=?",
                RoleCodeEnums.IN_MAMAGE.getCode(),platformId);
        for(Map item:searchResult){
            JSONObject re=new JSONObject();
            re.put("userId",item.get("user_id"));
            re.put("userName",item.get("user_name"));
            re.put("email",item.get("email"));
            re.put("telephone",item.get("telephone"));
            re.put("loginNo",item.get("login_no"));
            re.put("userActive",item.get("user_active"));
//            re.put("IsActive",StatusCodeEnums.ACTIVE.getCode().equals(String.valueOf(item.get("user_active")))?"已激活":"停用");
            result.add(re);
        }
        return result;
    }

    @Override
    public JSONObject changePlatformDetail(String newPlatformDetail,String platformId,String platformURL) {
        JSONObject result=new JSONObject();
        int resultRow=jdbcTemplateXAI.update("UPDATE sys_platform_info t set t.platform_detail=?,t.platform_url=? where t.platform_id=?",
                newPlatformDetail,platformURL,platformId);
        if(resultRow==1){
            result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg());
        }else{
            result= ResultUtils.commonResultJSON(ResultCodeEnums.UPDATE_ERROR.getCode(),ResultCodeEnums.UPDATE_ERROR.getMsg());
        }
        return  result;
    }

    @Override
    public JSONObject addNewMenuMeb(String menuId,String name,String path,String fatherId) {
        JSONObject result=new JSONObject();
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.menu_id FROM sys_menu_info t WHERE t.menu_id=?",menuId);
        if(searchResult.size()>0) {
            result = ResultUtils.commonResultJSON(ResultCodeEnums.MENU_EXIST.getCode(), ResultCodeEnums.MENU_EXIST.getMsg());
        }else {
            jdbcTemplateXAI.update("INSERT INTO sys_menu_info (menu_id,menu_level,menu_name,menu_father_id,menu_active,menu_path) VALUES(?,?,?,?,?,?)",
                    menuId, "2", name, fatherId, "1", path);
            //开发人员默认赋权
            jdbcTemplateXAI.update("INSERT INTO sys_role_menu (role_id,menu_id,role_platform_id) VALUES(?,?,?)",
                    "1", menuId, "0");
            result = ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg());
        }
        return  result;
    }

    @Override
    public JSONObject addNewFMenuMeb(String menuId,String name) {
        JSONObject result=new JSONObject();
        String path="";
        //判重
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.menu_id FROM sys_menu_info t WHERE t.menu_id=?",menuId);
        if(searchResult.size()>0) {
            result = ResultUtils.commonResultJSON(ResultCodeEnums.MENU_EXIST.getCode(), ResultCodeEnums.MENU_EXIST.getMsg());
        }else {
            jdbcTemplateXAI.update("INSERT INTO sys_menu_info (menu_id,menu_level,menu_name,menu_father_id,menu_active,menu_path) VALUES(?,?,?,?,?,?)",
                    menuId, "1", name, "0", "1", path);
            //开发人员默认赋权
            jdbcTemplateXAI.update("INSERT INTO sys_role_menu (role_id,menu_id,role_platform_id) VALUES(?,?,?)",
                    "1", menuId, "0");
            result = ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg());
        }
        return  result;
    }

    @Override
    public JSONObject changePlatformStatus(String platformId,String activeStatus) {
        JSONObject result=new JSONObject();
        String activeStatus1=activeStatus.equals(StatusCodeEnums.ACTIVE.getCode())?StatusCodeEnums.IN_ACTIVE.getCode():StatusCodeEnums.ACTIVE.getCode();
        int resultRow=jdbcTemplateXAI.update("UPDATE sys_platform_info t set t.platform_active=? where t.platform_id=?",
                activeStatus1,platformId);
        if(resultRow==1){
            if(activeStatus.equals(StatusCodeEnums.ACTIVE.getCode())){
                jdbcTemplateXAI.update("UPDATE sys_user_info t set t.user_active=? where t.user_platform_id=?",StatusCodeEnums.IN_ACTIVE.getCode(),platformId);
            }
            result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg());
        }else{
            result= ResultUtils.commonResultJSON(ResultCodeEnums.UPDATE_ERROR.getCode(),ResultCodeEnums.UPDATE_ERROR.getMsg());
        }
        return  result;
    }

    @Override
    public JSONObject changeMenuStatus(String menuId,String menuActive) {
        JSONObject result=new JSONObject();
        String menuActive1=menuActive.equals(StatusCodeEnums.ACTIVE.getCode())?StatusCodeEnums.IN_ACTIVE.getCode():StatusCodeEnums.ACTIVE.getCode();
        int resultRow=jdbcTemplateXAI.update("UPDATE sys_menu_info t set t.menu_active=? where t.menu_id=?",
                menuActive1,menuId);
        if(resultRow==1){
            result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg());
        }else{
            result= ResultUtils.commonResultJSON(ResultCodeEnums.UPDATE_ERROR.getCode(),ResultCodeEnums.UPDATE_ERROR.getMsg());
        }
        return  result;
    }

    @Override
    public JSONObject addNewPlatform(String platformName,String platformDetail,String platformAbbr) {
        JSONObject result=new JSONObject();
        String platformId=CoreUtils.getUUID();
        String platformActive=StatusCodeEnums.ACTIVE.getCode();
        int resultRow=jdbcTemplateXAI.update("INSERT INTO sys_platform_info (platform_id,platform_name,platform_detail,platform_abbr,platform_active) VALUES(?,?,?,?,?)",
                platformId,platformName,platformDetail,platformAbbr,platformActive);

        result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg());

        return  result;
    }

    @Override
    public JSONObject addNewOP(String opURL,String opDescribe,String opType) {
        JSONObject result=new JSONObject();
        //判重
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.op_url FROM sys_op_info t WHERE t.op_url=?",opURL.trim());
        if(searchResult.size()>0){
            result= ResultUtils.commonResultJSON(ResultCodeEnums.OP_EXIST.getCode(),ResultCodeEnums.OP_EXIST.getMsg());
        }else {
            jdbcTemplateXAI.update("INSERT INTO sys_op_info (op_url,op_type,op_describe) VALUES(?,?,?)",
                    opURL.trim(), opType, opDescribe);
            result = ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg());
        }
        return  result;
    }

    @Override
    public JSONObject changeUserStatus(String userId,String activeStatus) {
        JSONObject result=new JSONObject();
        String activeStatus1=activeStatus.equals(StatusCodeEnums.ACTIVE.getCode())?StatusCodeEnums.IN_ACTIVE.getCode():StatusCodeEnums.ACTIVE.getCode();
        int resultRow=jdbcTemplateXAI.update("UPDATE sys_user_info t set t.user_active=? where t.user_id=?",
                activeStatus1,userId);
        if(resultRow==1){
            result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg());
        }else{
            result= ResultUtils.commonResultJSON(ResultCodeEnums.UPDATE_ERROR.getCode(),ResultCodeEnums.UPDATE_ERROR.getMsg());
        }
        return  result;
    }

    @Override
    public JSONObject addNewManager(String userId,String userName,String platformId,String platformStatus,String email,String telephone,String loginNo) {
        JSONObject result=new JSONObject();
        String password= DigestUtils.md5DigestAsHex("123456".getBytes());
        String roleId= RoleCodeEnums.IN_MAMAGE.getCode();
        String userInfo= "";
        String creatTime=CoreUtils.getDateTime();

        //判重前端写了就先不写了

        jdbcTemplateXAI.update("INSERT INTO sys_user_info (user_id,user_name,password,role_id,user_info,user_platform_id,user_active,email,telephone,login_no,create_time) VALUES(?,?,?,?,?,?,?,?,?,?,?)",
                userId, userName, password, roleId, userInfo, platformId, platformStatus,email,telephone,loginNo,creatTime);
        result = ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg());

        return  result;
    }

    @Override
    public String[] getPlatformMenu(String platformId) {
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.menu_id from sys_role_menu t WHERE t.role_id=? and t.role_platform_id=?",RoleCodeEnums.IN_MAMAGE.getCode(),platformId);
        String[] result=new String[searchResult.size()];
        for(int i=0;i<searchResult.size();i++){
            result[i]= String.valueOf(searchResult.get(i).get("menu_id"));
        }
        return result;
    }

    @Override
    public JSONObject updatePlatformMenu(String platformId, String[] menuList) {
        JSONObject result=new JSONObject();

        jdbcTemplateXAI.update("DELETE from sys_role_menu WHERE role_id=? and role_platform_id=?",RoleCodeEnums.IN_MAMAGE.getCode(),platformId);
        List<Object[]> params=new ArrayList<>();
        for(String a:menuList){
            params.add(new Object[]{RoleCodeEnums.IN_MAMAGE.getCode(),a,platformId});
        }
        jdbcTemplateXAI.batchUpdate("INSERT INTO sys_role_menu (role_id,menu_id,role_platform_id) VALUES(?,?,?)",params);
        result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg());
        return  result;
    }

    //平台管理员系统操作
    //
    @Override
    public JSONArray getUserList(String platformId) {
        JSONArray result=new JSONArray();
        // 只看普通用户的
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.user_id,t.user_name,t.email,t.telephone,t.user_active,t.work_organization,t.country_id,t.province_id,t.city_id FROM sys_user_info t WHERE t.role_id=? and t.user_platform_id=?",
                RoleCodeEnums.IN_MAMAGE_P.getCode(),platformId);
        for(Map item:searchResult){
            JSONObject re=new JSONObject();
            re.put("userId",item.get("user_id"));
            re.put("userName",item.get("user_name"));
            re.put("email",item.get("email"));
            re.put("telephone",item.get("telephone"));
            re.put("workOrganization",item.get("work_organization"));
            re.put("countryNo",item.get("country_id"));
            re.put("provinceNo",item.get("province_id"));
            re.put("cityNo",item.get("city_id"));
            re.put("userActive",item.get("user_active"));
            result.add(re);
        }
        return result;
    }

    @Override
    public JSONObject addNewUser(String userId,String userName,String platformId,String email,String telephone,String workOrganization,String countryNo,JSONArray provinceAndCityNo) {
        JSONObject result=new JSONObject();
        String password= DigestUtils.md5DigestAsHex("123456".getBytes());
        String roleId= RoleCodeEnums.IN_MAMAGE_P.getCode();
        String userInfo= "";
        String creatTime=CoreUtils.getDateTime();
        if(provinceAndCityNo.size()==0) {
            jdbcTemplateXAI.update("INSERT INTO sys_user_info (user_id,user_name,password,role_id,user_info,user_platform_id,user_active,email,telephone,create_time,work_organization,country_id) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
                    userId, userName, password, roleId, userInfo, platformId, StatusCodeEnums.ACTIVE.getCode(), email, telephone, creatTime,workOrganization,countryNo);
        }else {
            String provinceNo= String.valueOf(provinceAndCityNo.get(0));
            String cityNo= String.valueOf(provinceAndCityNo.get(1));
            jdbcTemplateXAI.update("INSERT INTO sys_user_info (user_id,user_name,password,role_id,user_info,user_platform_id,user_active,email,telephone,create_time,work_organization,country_id,province_id,city_id) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    userId, userName, password, roleId, userInfo, platformId, StatusCodeEnums.ACTIVE.getCode(), email, telephone, creatTime,workOrganization,countryNo,provinceNo,cityNo);
        }

        jdbcTemplateXAI.update("INSERT INTO sys_puser_menu(user_id,menu_id) SELECT ?,t.menu_id from sys_role_menu t WHERE t.role_id=? and t.role_platform_id=?",userId,roleId,platformId);

        result = ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg());

        return  result;
    }

    @Override
    public JSONArray getPlatformMenuData(String platformId) {
        JSONArray result=new JSONArray();
        List<Map<String, Object>> searchResult= jdbcTemplateXAI.queryForList(
                    "select t1.menu_id,t1.menu_father_id,t1.menu_level,t1.menu_name,t1.menu_path from sys_menu_info t1 \n" +
                            "LEFT JOIN sys_role_menu t2 on t1.menu_id=t2.menu_id  \n" +
                            "WHERE t1.menu_active=? and t2.role_id=? and t2.role_platform_id=?",
                    StatusCodeEnums.ACTIVE.getCode(),RoleCodeEnums.IN_MAMAGE.getCode(),platformId);

        //二级树结构生成
        result= CoreUtils.getMenuTree(searchResult);
        return result;
    }

    @Override
    public String[] getUserMenu(String userId) {
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.menu_id from sys_puser_menu t WHERE t.user_id=?",userId);
        String[] result=new String[searchResult.size()];
        for(int i=0;i<searchResult.size();i++){
            result[i]= String.valueOf(searchResult.get(i).get("menu_id"));
        }
        return result;
    }

    @Override
    public JSONObject updateUserMenu(String userId, String[] menuList) {
        JSONObject result=new JSONObject();

        jdbcTemplateXAI.update("DELETE from sys_puser_menu WHERE user_id=?",userId);
        List<Object[]> params=new ArrayList<>();
        for(String a:menuList){
            params.add(new Object[]{userId,a});
        }
        jdbcTemplateXAI.batchUpdate("INSERT INTO sys_puser_menu (user_id,menu_id) VALUES(?,?)",params);
        result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg());
        return  result;
    }

    @Override
    public String[] getRegisterUserMenu(String platformId) {
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.menu_id from sys_role_menu t WHERE t.role_platform_id=? and t.role_id=?",platformId,RoleCodeEnums.IN_MAMAGE_P.getCode());
        String[] result=new String[searchResult.size()];
        for(int i=0;i<searchResult.size();i++){
            result[i]= String.valueOf(searchResult.get(i).get("menu_id"));
        }
        return result;
    }

    @Override
    public JSONObject updateRegisterUserMenu(String[] menuList, String platformId) {
        JSONObject result=new JSONObject();

        jdbcTemplateXAI.update("DELETE from sys_role_menu WHERE role_platform_id=? and role_id=?",platformId,RoleCodeEnums.IN_MAMAGE_P.getCode());
        List<Object[]> params=new ArrayList<>();
        for(String a:menuList){
            params.add(new Object[]{RoleCodeEnums.IN_MAMAGE_P.getCode(),a,platformId});
        }
        jdbcTemplateXAI.batchUpdate("INSERT INTO sys_role_menu (role_id,menu_id,role_platform_id) VALUES(?,?,?)",params);

        result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg());
        return  result;
    }


    //普通用户系统操作
    @Override
    public JSONObject userRegister3(String platformId,String userName, String email, String password,String telephone, String userInfo, String occupation, String researchDomain, String researchDirection, String workOrganization,String countryNo,JSONArray provinceAndCityNo) {
        JSONObject result=new JSONObject();
        //系统参数
        String userId= CoreUtils.getUUID();
        String roleId= RoleCodeEnums.IN_MAMAGE_P.getCode();
        String creatTime=CoreUtils.getDateTime();

        // 发送邮件
        boolean status =emailUtils.sendCode(email, platformId);
        if (!status){
            result = ResultUtils.commonResultJSON(ResultCodeEnums.EMAIL_FAIL.getCode(), ResultCodeEnums.EMAIL_FAIL.getMsg());
        }else {
            if(provinceAndCityNo.size()==0) {
                //只存所在国家
                jdbcTemplateXAI.update("INSERT INTO sys_user_info (user_id,user_name,password,role_id,user_info,user_platform_id,user_active,email,occupation,research_domain,research_direction,work_organization,create_time,telephone,country_id) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        userId, userName, password, roleId, userInfo, platformId, StatusCodeEnums.IN_ACTIVE.getCode(), email, occupation, researchDomain, researchDirection, workOrganization, creatTime, telephone, countryNo);
            }else {
                //中国存国家、省、市
                String provinceNo= String.valueOf(provinceAndCityNo.get(0));
                String cityNo= String.valueOf(provinceAndCityNo.get(1));
                jdbcTemplateXAI.update("INSERT INTO sys_user_info (user_id,user_name,password,role_id,user_info,user_platform_id,user_active,email,occupation,research_domain,research_direction,work_organization,create_time,telephone,country_id,province_id,city_id) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        userId, userName, password, roleId, userInfo, platformId, StatusCodeEnums.IN_ACTIVE.getCode(), email, occupation, researchDomain, researchDirection, workOrganization, creatTime, telephone, countryNo,provinceNo,cityNo);
            }
            jdbcTemplateXAI.update("INSERT INTO sys_puser_menu(user_id,menu_id) SELECT ?,t.menu_id from sys_role_menu t WHERE t.role_id=? and t.role_platform_id=?",userId,roleId,platformId);

            result = ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg());
        }
        return  result;
    }






    //其他
    @Override
    public String getUserStatus(String userId){
        String status=jdbcTemplateXAI.queryForObject("select t.user_active from sys_user_info t WHERE t.user_id=?",new Object[]{userId},String.class);
        return status;
    }

    @Override
    public JSONObject valRepeatEmail(String email,String platformId){
        JSONObject result=new JSONObject();
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.user_id FROM sys_user_info t WHERE t.email=? and t.user_platform_id=?",email,platformId);
        if(searchResult.size()>0) {
            result = ResultUtils.commonResultJSON(ResultCodeEnums.EMAIL_EXIST.getCode(), ResultCodeEnums.EMAIL_EXIST.getMsg());
        }else {
            result = ResultUtils.commonJSONSuccess("");
        }
        return result;
    }
    @Override
    public JSONObject valRepeatTelephone(String telephone,String platformId){
        JSONObject result=new JSONObject();
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.user_id FROM sys_user_info t WHERE t.telephone=? and t.user_platform_id=?",telephone,platformId);
        if(searchResult.size()>0) {
            result = ResultUtils.commonResultJSON(ResultCodeEnums.TELEPHONE_EXIST.getCode(), ResultCodeEnums.TELEPHONE_EXIST.getMsg());
        }else {
            result = ResultUtils.commonJSONSuccess("");
        }
        return result;
    }
    @Override
    public JSONObject valRepeatLoginNo(String loginNo){
        JSONObject result=new JSONObject();
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.user_id FROM sys_user_info t WHERE t.login_no=?",loginNo);
        if(searchResult.size()>0) {
            result = ResultUtils.commonResultJSON(ResultCodeEnums.LOGINNO_EXIST.getCode(), ResultCodeEnums.LOGINNO_EXIST.getMsg());
        }else {
            result = ResultUtils.commonJSONSuccess("");
        }
        return result;
    }
    @Override
    public JSONObject valRepeatUserName(String userName,String platformId){
        JSONObject result=new JSONObject();
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.user_id FROM sys_user_info t WHERE t.user_name=? and t.user_platform_id=?",userName,platformId);
        if(searchResult.size()>0) {
            result = ResultUtils.commonResultJSON(ResultCodeEnums.USERNAME_EXIST.getCode(), ResultCodeEnums.USERNAME_EXIST.getMsg());
        }else {
            result = ResultUtils.commonJSONSuccess("");
        }
        return result;
    }

    @Override
    public JSONObject valRepeatMenuId(String menuId){
        JSONObject result=new JSONObject();
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.menu_id FROM sys_menu_info t WHERE t.menu_id=?",menuId);
        if(searchResult.size()>0) {
            result = ResultUtils.commonResultJSON(ResultCodeEnums.MENUID_EXIST.getCode(), ResultCodeEnums.MENUID_EXIST.getMsg());
        }else {
            result = ResultUtils.commonJSONSuccess("");
        }
        return result;
    }

    @Override
    public JSONObject valRepeatMenuPath(String menuPath){
        JSONObject result=new JSONObject();
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.menu_id FROM sys_menu_info t WHERE t.menu_path=?",menuPath);
        if(searchResult.size()>0) {
            result = ResultUtils.commonResultJSON(ResultCodeEnums.MENUPATH_EXIST.getCode(), ResultCodeEnums.MENUPATH_EXIST.getMsg());
        }else {
            result = ResultUtils.commonJSONSuccess("");
        }
        return result;
    }

    @Override
    public JSONObject registerActive(String mass) {
        //mass解码
        String decode=new String(Base64.getDecoder().decode(mass), StandardCharsets.UTF_8);
        String[] a=decode.split(",");
        String email=a[0];
        String platformId=a[1];
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.user_id FROM sys_user_info t WHERE t.email=? and t.user_platform_id=? and t.user_active=?",email,platformId,StatusCodeEnums.ACTIVE.getCode());
        if(searchResult.size()>0){
            return ResultUtils.commonResultJSON(ResultCodeEnums.REPEAT_ACTIVE.getCode(),ResultCodeEnums.REPEAT_ACTIVE.getMsg());
        }else {
            jdbcTemplateXAI.update("UPDATE sys_user_info t set t.user_active=? where t.email=? and t.user_platform_id=?",StatusCodeEnums.ACTIVE.getCode(),email,platformId);
            return ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(),"激活成功，请重新访问登录页面登录");
        }
    }


    @Override
    public JSONObject getSinglePlatformInfo(String platformAbbr){
        JSONObject result=new JSONObject();
        List<Map<String,Object>> searchResult=jdbcTemplateXAI.queryForList("SELECT t.platform_id from sys_platform_info t where t.platform_abbr=?",platformAbbr);
        result.put("platformId",searchResult.get(0).get("platform_id"));
        return result;
    }

}
