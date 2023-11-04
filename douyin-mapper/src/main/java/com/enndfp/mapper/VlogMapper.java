package com.enndfp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enndfp.pojo.Vlog;
import com.enndfp.vo.VlogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Enndfp
 */
public interface VlogMapper extends BaseMapper<Vlog> {

    List<VlogVO> getIndexVlogList(@Param("paramMap")Map<String,Object> map);

}




