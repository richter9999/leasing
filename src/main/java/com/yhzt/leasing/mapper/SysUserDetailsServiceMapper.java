package com.yhzt.leasing.mapper;

import java.util.List;

import com.yhzt.leasing.config.security.SysUserDetails;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserDetailsServiceMapper {
  /**
     * 根据 userId 获取 用户信息
     * @param userId
     * @return
     */
    @Select("SELECT T.EMP_CODE AS 'username' "
            + " ,T.PASSWORD AS 'password' "
            + " ,T.USE_YN AS 'enable' "
            + " FROM SYS_EMP T "
            + " WHERE T.EMP_CODE = #{userId} ")
    SysUserDetails getUserByName(@Param("userId") String userId);

    /**
     * 根据 userId  获取 权限代码
     * @param userId
     * @return
     */
    @Select("SELECT T1.ROLE_CODE AS role_code "
            + " FROM SYS_ROLE T1 "
            + " LEFT JOIN SYS_EMP_ROLE_MAP T2 ON T1.ROLE_ID = T2.ROLE_ID "
            + " LEFT JOIN SYS_EMP T3 ON T3.EMP_ID = T2.EMP_ID "
            + " WHERE T3.EMP_CODE = #{userId} ")
    List<String> getRoleByUserName(@Param("userId") String userId);

    /**
     * 根据 权限代码 获取权限
     * @param roleCode
     * @return
     */
    @Select("<script>"
            + " SELECT T1.MENU_URL AS menuUrl "
            + " FROM SYS_MENU T1  "
            + " INNER JOIN SYS_ROLE_MENU_MAP T2 ON T1.MENU_ID = T2.MENU_ID "
            + " INNER JOIN SYS_ROLE T3 ON T2.ROLE_ID = T3.ROLE_ID "
            + " WHERE 1 = 1 "
            + " AND T3.ROLE_CODE IN  "
            + " <foreach collection='roleCodeList' item='roleCode' open='(' separator=',' close=')' > "
            + "     #{roleCode} "
            + " </foreach> "
            + "</script>")
    List<String> getAuthenticationByRoleCode(@Param("roleCodeList") List<String> roleCodeList);

    /**
     * 根据用户 返回 权限菜单
     * @param userId
     * @return
     */
    @Select("SELECT T1.MENU_URL AS menuUrl "
            + " FROM SYS_MENU T1 "
            + " INNER JOIN SYS_ROLE_MENU_MAP T2 ON T1.MENU_ID = T2.MENU_ID "
            + " INNER JOIN SYS_ROLE T3 ON T2.ROLE_ID = T3.ROLE_ID "
            + " INNER JOIN SYS_EMP_ROLE_MAP T4 ON T3.ROLE_ID = T4.ROLE_ID "
            + " INNER JOIN SYS_EMP T5 ON T4.EMP_ID = T5.EMP_ID "
            + " AND T5.EMP_CODE = #{userId} " )
    List<String> getAuthenticationByName(@Param("userId") String userId);
}