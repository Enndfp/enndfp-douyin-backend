package com.enndfp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enndfp.pojo.Fans;
import com.enndfp.vo.FansVO;
import com.enndfp.vo.VlogerVO;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author Enndfp
 */
public interface FansMapper extends BaseMapper<Fans> {

    Page<VlogerVO> queryMyFollows(Page<VlogerVO> page, @Param("paramMap") Map<String, Object> map);

    Page<FansVO> queryMyFans(Page<FansVO> page, @Param("paramMap") Map<String, Object> map);

}




