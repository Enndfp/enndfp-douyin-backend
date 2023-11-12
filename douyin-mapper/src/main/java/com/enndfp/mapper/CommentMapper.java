package com.enndfp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enndfp.pojo.Comment;
import com.enndfp.vo.CommentVO;
import com.enndfp.vo.VlogVO;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author Enndfp
 */
public interface CommentMapper extends BaseMapper<Comment> {
    Page<CommentVO> getCommentList(Page<CommentVO> page, @Param("paramMap") Map<String, Object> map);

}




