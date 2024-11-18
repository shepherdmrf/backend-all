package shu.xai.sys.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by yuziyi on 2021/6/20.
 */
public interface UserService {
    //开发人员系统操作
    JSONObject login(String loginWay, String loginAccount, String password,String platformId);
    JSONArray getMenuData(String userId,String roleId);
    JSONArray getMenuManageData();
    JSONArray getOPInfo();
    JSONObject getUserInfo(String userId);
    JSONObject changeUserInfo(String userId, String userInfo,String occupation,String researchDomain,String researchDirection,String workOrganization,String countryNo,JSONArray provinceAndCityNo);
    JSONObject resetPassword(String userId, String newPassword);
    JSONArray getPlatformInfo();
    JSONArray platformManagers(String platformId);
    JSONObject changePlatformDetail(String newPlatformDetail,String platformId,String platformURL);
    JSONObject changePlatformStatus(String platformId,String activeStatus);
    JSONObject changeMenuStatus(String menuId,String menuActive);
    JSONObject addNewMenuMeb(String menuId,String name,String path,String fatherId);
    JSONObject addNewFMenuMeb(String menuId,String name);
    JSONObject addNewPlatform(String platformName,String platformDetail,String platformAbbr);
    JSONObject addNewOP(String opURL,String opDescribe,String opType);
    JSONObject changeUserStatus(String userId,String activeStatus);
    JSONObject addNewManager(String userId,String userName,String platformId,String platformStatus,String email,String telephone,String loginNo);
    String[] getPlatformMenu(String platformId);
    JSONObject updatePlatformMenu(String platformId,String[] menuList);

    //平台管理员系统操作
    JSONArray getUserList(String platformId);
    JSONObject addNewUser(String userId,String userName,String platformId,String email,String telephone,String workOrganization,String countryNo,JSONArray provinceAndCityNo);
    JSONArray getPlatformMenuData(String platformId);
    String[] getUserMenu(String userId);
    JSONObject updateUserMenu(String userId,String[] menuList);
    String[] getRegisterUserMenu(String platformId);
    JSONObject updateRegisterUserMenu(String[] menuList,String platformId);



    //平台普通用户系统操作
    JSONObject userRegister3(String platformId,String userName,String email,String password,String telephone,String userInfo,String occupation,String researchDomain,String researchDirection,String workOrganization,String countryNo,JSONArray provinceAndCityNo);



    //其他
    String getUserStatus(String userId);
    JSONObject valRepeatEmail(String email,String platformId);
    JSONObject valRepeatTelephone(String telephone,String platformId);
    JSONObject valRepeatLoginNo(String loginNo);
    JSONObject valRepeatUserName(String userName,String platformId);
    JSONObject valRepeatMenuId(String menuId);
    JSONObject valRepeatMenuPath(String menuPath);
    JSONObject registerActive(String mass);
    JSONObject getSinglePlatformInfo(String platformAbbr);





}
