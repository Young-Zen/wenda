package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.WendaUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    SensitiveService sensitiveService;

    public Map<String,Object> register(String username, String password){
        Map<String,Object> map=new HashMap<String, Object>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(username.length()>64){
            map.put("msg","用户名长度不能超过64");
            return map;
        }
        if(sensitiveService.isContainSensitive(username)!=null){
            map.put("msg","用户名不能含有敏感词："+sensitiveService.isContainSensitive(username));
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        if(password.length()>128){
            map.put("msg","密码长度不能超过128");
            return map;
        }
        User user=userDAO.selectByName(username);
        if(user!=null){
            map.put("msg","用户名已经被注册");
            return map;
        }
        user=new User();

        user.setName(HtmlUtils.htmlEscape(username));
        user.setName(sensitiveService.filter(user.getName()));

        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);
        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public Map<String,Object> login(String username, String password){
        Map<String,Object> map=new HashMap<String, Object>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        User user=userDAO.selectByName(username);
        if(user==null){
            map.put("msg","用户名不存在");
            return map;
        }
        if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码错误");
            return map;
        }
        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        map.put("userId",user.getId());
        return map;
    }

    private String addLoginTicket(int userId){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(userId);
        Date now=new Date();
        now.setTime(1000*3600*24+now.getTime());
        loginTicket.setExpired(now);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }

    public User getUser(int id){
        return userDAO.selectById(id);
    }

    public User selectByName(String name){
        return userDAO.selectByName(name);
    }
}
