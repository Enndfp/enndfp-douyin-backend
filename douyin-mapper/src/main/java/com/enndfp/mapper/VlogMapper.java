package com.enndfp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enndfp.pojo.Vlog;
import com.enndfp.vo.VlogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Enndfp
 */
public interface VlogMapper extends BaseMapper<Vlog> {

    Page<VlogVO> getIndexVlogList(Page<VlogVO> page, @Param("paramMap") Map<String, Object> map);

    VlogVO getVlogDetailById(@Param("paramMap") Map<String, Object> map);

    Page<VlogVO> getMyLikedVlogList(Page<VlogVO> page, @Param("paramMap") Map<String, Object> map);

}




