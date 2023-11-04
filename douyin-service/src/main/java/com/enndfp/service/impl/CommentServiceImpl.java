package com.enndfp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enndfp.mapper.CommentMapper;
import com.enndfp.pojo.Comment;
import com.enndfp.service.CommentService;
import org.springframework.stereotype.Service;

/**
 * @author Enndfp
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService {

}




