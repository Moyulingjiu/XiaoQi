package top.beforedawn.service.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import top.beforedawn.service.model.po.BlacklistPo;
import top.beforedawn.service.model.po.BlacklistPoExample;

public interface BlacklistPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table blacklist
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table blacklist
     *
     * @mbg.generated
     */
    int insert(BlacklistPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table blacklist
     *
     * @mbg.generated
     */
    int insertSelective(BlacklistPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table blacklist
     *
     * @mbg.generated
     */
    List<BlacklistPo> selectByExample(BlacklistPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table blacklist
     *
     * @mbg.generated
     */
    BlacklistPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table blacklist
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") BlacklistPo record, @Param("example") BlacklistPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table blacklist
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") BlacklistPo record, @Param("example") BlacklistPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table blacklist
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(BlacklistPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table blacklist
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(BlacklistPo record);
}