package com.ezen.book.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ezen.book.domain.MemberVO;
import com.ezen.book.domain.OrderVO;
import com.ezen.book.domain.PagingVO;
import com.ezen.book.handler.PagingHandler;
import com.ezen.book.service.MemberService;
import com.ezen.book.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/mem/*")
@Controller
public class MemberController {
	
	@Inject
	private OrderService osv;
	
	@Inject
	private MemberService msv;

	@GetMapping({"loginPage","login-member"})
	public String loginPage() {
		return "/member/memberLogin";
	}
	@GetMapping("login-Non-member")
	public String loginNonPage() {
		return "/member/memberLoginNon";
	}

	@GetMapping("joinPage")
	public String joinPage() {
		return "/member/memberJoin";
	}

	

	@PostMapping("/login")
	   public String login(MemberVO mvo, RedirectAttributes reAttr, HttpServletRequest req) {
	      MemberVO mvo2 = msv.login(mvo.getMem_id(), mvo.getMem_pw());
	      log.info("login getMem_id : "+mvo.getMem_id());
	      log.info("login getMem_pw : "+mvo.getMem_pw());
	      if (mvo2 != null) {
	         HttpSession ses = req.getSession();
	         ses.setAttribute("ses", mvo2);
	         ses.setMaxInactiveInterval(60*10);
	         return "redirect:/";
	      } else {
	         reAttr.addFlashAttribute("msg", "0");
	         return "redirect:/mem/loginPage";
	      }

	   }

	@GetMapping("/logOut")
	public String logout(Model model, HttpServletRequest req, RedirectAttributes reAttr) {
		req.getSession().removeAttribute("ses");
		req.getSession().invalidate();

		return "redirect:/";

	}
	
	@GetMapping("/naverApi")
	public String naverApiPage() {
		return "/member/memberNaverlogin";
	}
	@GetMapping("/callback")
	public String naverApiCallback() {
		return "/member/memberCallBack";
	}
	@GetMapping("/mypage")
		public String mypage(){
	
			return "/member/memberMypage";
	}
	
	@GetMapping("/modify")
	public String modify(HttpServletRequest req) {
		return "/member/memberModify";
	}
	
	@PostMapping("/modify")
	public  String modifyPost(MemberVO mvo,HttpServletRequest req) {		
		log.info("mvo>>>"+mvo.toString());		
		int isUp=msv.usermodify(mvo);
		log.info(">>>modify:"+(isUp>0?"ok":"fail"));
		req.getSession().removeAttribute("ses");
		req.getSession().invalidate();

		
		return "redirect:/";
	}
	
	@PostMapping("/join")
	public String join(MemberVO mvo) {
		log.info(">>> member join check 1");

		boolean isUp = msv.join(mvo);
		if (!isUp) {
			return "redirect:/member/memberJoin";
		}
		log.info(">>> member join ??????");
		return "redirect:/";
	}
	
	
	@GetMapping("/orderCheck")
	public String orderList(Model model,PagingVO pvo,@RequestParam("mem_num")int mem_num){
		log.info(">>>pageNo :"+pvo.getPageNo());
		log.info(">>>num :"+mem_num);
		String status="??????";
		List<OrderVO> list=osv.getList(pvo,status,mem_num);
		model.addAttribute("list", list);
		int totalCount=osv.getTotalCount(pvo);
		PagingHandler ph = new PagingHandler(pvo,totalCount);
		model.addAttribute("pgh",ph);
		return "/member/memberOrderCheck";
	}
	
	@GetMapping("/buyCheck")
	public String buyList(Model model,PagingVO pvo,@RequestParam("mem_num")int mem_num){
		log.info(">>>pageNo :"+pvo.getPageNo());
		String status="??????";
		List<OrderVO> list=osv.getList(pvo,status,mem_num);
		
		model.addAttribute("list", list);
		int totalCount=osv.getTotalCount(pvo);
		PagingHandler ph = new PagingHandler(pvo,totalCount);
		model.addAttribute("pgh",ph);
		return "/member/memberBuyCheck";
	}
	@GetMapping("/mantoman")
	public String getManToMan() {
		return"/member/memberMantoman";
	}
	
	@GetMapping("/delete")
	public String getDelte() {
		return"/member/memberDelete";
	}
	
	@PostMapping("/delete")
	public String MemberDelete(Model model, HttpServletRequest req, RedirectAttributes reAttr,@RequestParam("mem_num")int mem_num){
		log.info("mem_num"+mem_num);
		int deleteOk=msv.deleteMember(mem_num);
		req.getSession().removeAttribute("ses");
		req.getSession().invalidate();
		return "/home";
		
	}
	
	@PostMapping("pwCheck")//??????????????? pw??? null,??????????????? ?????? ?????????
	   @ResponseBody  
	   public String pwCheck(MemberVO mvo) {
	      String isOk = msv.pwCheck(mvo.getMem_pw());
	      log.info(mvo.getMem_pw());
	      log.info("?????? ?????? isok : "+isOk);
	      return isOk;
	   }
	

	
}
