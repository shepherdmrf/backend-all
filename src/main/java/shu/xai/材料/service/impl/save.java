package shu.xai.材料.service.impl;

public class save {
}
//@Service
//public class PlatformServiceImpl implements PlatformService {
//
//    @Resource(name = "jdbcTemplatefeaturetable") // 正确注入 JdbcTemplate Bean
//    private JdbcTemplate jdbcTemplatefeature;
//
//    public JSONArray GetFeatureTable(String userId, String roleId) {
//        JSONArray result = new JSONArray(); // 在方法内部初始化 JSONArray 以确保线程安全性
//        List<Map<String, Object>> searchResult;
//
//        // 检查角色是否为开发者或管理者
//        if (roleId.equals(RoleCodeEnums.KAIFA.getCode()) || roleId.equals(RoleCodeEnums.IN_MAMAGE.getCode())) {
//            try {
//                // 执行 SQL 查询
//                searchResult = jdbcTemplatefeature.queryForList("select * from gpr_feature_table");
//
//                // 将查询结果转换为 JSONArray
//                for (Map<String, Object> row : searchResult) {
//                    JSONObject jsonObject = new JSONObject(row);
//                  result.add(jsonObject) ; // 将每一行数据转换为 JSONObject 并添加到 JSONArray
//                }
//            } catch (DataAccessException e) {
//                // 处理数据库异常，例如记录日志或抛出自定义异常
//                e.printStackTrace();
//            }
//        }
//
//        return result; // 返回填充后的 JSONArray
//    }
//}
//