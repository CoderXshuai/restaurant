package com.lzy.liujing.restaurant.controller.sysController;

import com.lzy.liujing.restaurant.Enums.ResultEnum;
import com.lzy.liujing.restaurant.entity.*;
import com.lzy.liujing.restaurant.service.MemberCategoryService;
import com.lzy.liujing.restaurant.service.MemberService;
import com.lzy.liujing.restaurant.utils.ResultUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberCategoryService memberCategoryService;
    /**
     * 用户列表html页面
     * @param model
     * @return
     */
    @RequiresPermissions("manage:cashier:view")
    @GetMapping("/list.html")
    public String memberList(Model model){
        model.addAttribute("memberCategoryList",memberCategoryService.findAll());
        SysUser user = (SysUser) SecurityUtils.getSubject().getSession().getAttribute("user");
        model.addAttribute("role",user.getRole());
        return "/member/list";
    }

    @RequiresPermissions("manage:cashier:edit")
    @GetMapping("/add.html")
    public String addMember(Model model){
        model.addAttribute("memberCategoryList",memberCategoryService.findAll());
        return "/member/add";
    }

    @RequiresPermissions("manage:cashier:edit")
    @PostMapping("/add.do")
    @ResponseBody
    public Result<Member> addMember(Member member,MemberCategory memberCategory){
        member.setMemberCategory(memberCategory);
        memberService.addMember(member);
        return ResultUtil.success();
    }

    @RequiresPermissions("manage:cashier:edit")
    @GetMapping("/edit.html/{memberId}")
    public String editMember(@PathVariable("memberId") Long memberId, Model model){
        Member member = new Member();
        member.setMemberId(memberId);
        Member resultMember = memberService.findMemberById(member);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String birthday = sf.format(resultMember.getBirthday());
        model.addAttribute("member",resultMember);
        model.addAttribute("birthday",birthday);
        model.addAttribute("memberCategoryList",memberCategoryService.findAll());
        return "/member/edit";
    }

    @RequiresPermissions("manage:cashier:edit")
    @PostMapping("/edit.do")
    @ResponseBody
    public Result<Member> editMember(Member member,MemberCategory memberCategory){
        member.setMemberCategory(memberCategory);
        memberService.update(member);
        return ResultUtil.success();
    }

    @RequiresPermissions("manage:del")
    @PostMapping("/del.do")
    @ResponseBody
    public Result<Member> deleteMember(@RequestParam String ids){
        memberService.deleteByIds(ids);
        return ResultUtil.success();
    }
    /**
     * 会员列表数据接口
     * @param pageInfo 接收pageNum、pageSize
     * @param member 接收条件信息
     * @param memberCategory 接收类型条件的id
     * @return
     */

    @RequiresPermissions("manage:cashier:view")
    @PostMapping("/list.do")
    @ResponseBody
    public Result<List<Member>> memberList(CustomPageInfo<Member> pageInfo,Member member,
                                           MemberCategory memberCategory){
        member.setMemberCategory(memberCategory);
        pageInfo.setT(member);
        CustomPageInfo<Member> resultPageInfo = memberService.findPage(pageInfo);
        Result<List<Member>> result = new Result<>();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMsg(ResultEnum.SUCCESS.getMsg());
        result.setCount(resultPageInfo.getTotal());
        result.setData(resultPageInfo.getList());
        return result;
    }
}
