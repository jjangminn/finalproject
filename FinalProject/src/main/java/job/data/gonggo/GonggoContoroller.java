package job.data.gonggo;



import java.io.File;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;




@Controller
public class GonggoContoroller {
	  @Autowired
	  	CompanyMapper mapper;
		String uploadName;//photo 폴더에 업로드 되는 실제 사진 파일명

	   @GetMapping("/gonggolist")
	   public ModelAndView index() {
	      ModelAndView mview =new ModelAndView();
	      //목록 가져오기
	      List<CompanyDto> gonggolist=mapper.getAlldatas();
	      Date date=new Date();
          long time= date.getTime();
          
          mview.addObject("date", date);
          mview.addObject("time",time);
	      mview.addObject("gonggolist",gonggolist);
	      
	      for(CompanyDto d:gonggolist)
	    	  System.out.println(d.getDeadline());
	      
	      mview.setViewName("/gonggo/gonggolist");
	      return mview;
	   }
	   @GetMapping({"/gonggodetail"})
	   public ModelAndView gonggo(String num)
	   {
		   ModelAndView mview=new ModelAndView();
		   	CompanyDto dto=new CompanyDto();
		    dto= mapper.getData(num);
			mview.addObject("dto",dto);
			
			List<CategoryDto>cdto=dto.getCategory();
			mview.addObject("cdto",cdto);
			mview.addObject("num",dto.getNum());

			mview.setViewName("/gonggo/gonggodetail");
			return mview;
	   }
	   
		/*
		 * @PostMapping("/detail") public CompanyDto getData(String num) { return
		 * mapper.getData(num); }
		 */
	   @GetMapping({"/writegonggo"})
	   public ModelAndView insertform()
	   {
		   ModelAndView mview =new ModelAndView();
		   	  String cate[] = {"업계연봉수준","보상","출퇴근","식사/간식","기업문화"};
		   	  mview.setViewName("/gonggo/writegonggo");
		      return mview;
		   }
	   
	   @PostMapping("/insert")
	   public String insertgonggo(
			   @ModelAttribute CompanyDto dto,
			   @ModelAttribute CategoryDto category,
			   HttpServletRequest request)
	   {
		   String path=request.getSession().getServletContext().getRealPath("/gonggophoto"); 
		   System.out.println(path); 
		   
		   SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		   String fileName="photo"+sdf.format(new Date())+"_"+dto.getUpload().getOriginalFilename();
		   //파일명= "photo"+날짜(년월일시분초)+dto에 업로드된 실제 파일이름
		   dto.setEmpimg(fileName);
		   MultipartFile uploadFile=dto.getUpload();
		   try { 
				 uploadFile.transferTo(new File(path+"\\"+fileName));
				} catch(IllegalStateException | IOException e) { 
					e.printStackTrace(); } 
		   
		   //db insert 
			mapper.insertGonggo(dto);
			int num=mapper.getInsertNum();
			category.setNum(num);
			
			String ctg[]=category.getCtg().split(",",-1);
			String tag[]=category.getTag().split(",",-1);
			
			for(int i=0; i<ctg.length; i++) {
				category.setCtg(ctg[i]);
				category.setTag(tag[i]);
				mapper.insertCategory(category);
			}

			return "redirect:gonggolist"; 
	   }
		
	
		   
	   
	   
	   @GetMapping("/delete")
	   public String delete(@RequestParam String num)
	   {
		   mapper.deleteGonggo(num);
		   mapper.deleteCategory(num);
		   return "redirect:gonggolist";
	   }
	   

	   @GetMapping({"/updategonggo"})
	   public ModelAndView updateform(@RequestParam String num)
	   {
		   ModelAndView mview=new ModelAndView();
		   CompanyDto dto=mapper.getData(num);
		   
		   List<CategoryDto>category=dto.getCategory();
		    mview.addObject("dto",dto);
			mview.addObject("category",category);
			mview.setViewName("/gonggo/updateform");
			return mview;
	   }
	   
	   @PostMapping("/update")
	   public ModelAndView update(@ModelAttribute CompanyDto dto,
			   @ModelAttribute CategoryDto category,
			   @RequestParam String num,
				HttpServletRequest request)
	   {
		   String path=request.getSession().getServletContext().getRealPath("/gonggophoto");
			System.out.println(path);
			String f=dto.getUpload().getOriginalFilename();
			//파일명 앞에 붙일 날짜 구하기
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			String fileName="photo"+sdf.format(new Date())+"_"+dto.getUpload().getOriginalFilename();
			if(f.equals("")){
				CompanyDto mto=mapper.getData(num);
				dto.setEmpimg(mto.getEmpimg());
				}else{
				//파일명= "photo"+ 날짜(년월일시분초)+dto에 업로드된 실제 파일이름
				//dto에 업로드될 파일명 저장
				dto.setEmpimg(fileName);
				//업로드 transferTo : 업로드한 파일 데이터를 지정한 파일에 저장
				MultipartFile uploadFile=dto.getUpload();
					try {
						uploadFile.transferTo(new File(path+"\\"+fileName));
						} catch (IllegalStateException | IOException e) {
							e.printStackTrace();
						}
				}
				ModelAndView mview = new ModelAndView();
				mapper.updateGonggo(dto);
				int cnum=dto.getNum();
				category.setNum(cnum);
				String ctg[]=category.getCtg().split(",",-1);
				//String ctg_idx[]=category.getCtg_idx().split(",",-1);
				String tag[]=category.getTag().split(",",-1);
				//String tag_idx[]=category.getTag_idx().split(",",-1);
				
				mapper.deleteCategory(num);
				for(int i=0; i<ctg.length; i++) {
					category.setCtg(ctg[i]);
					//category.setCtg_idx(ctg_idx[i]);
					category.setTag(tag[i]);
					//category.setTag_idx(tag_idx[i]);
					mapper.insertCategory(category);
				}
			
			mview.addObject("num",num);
			mview.setViewName("redirect:gonggodetail?num="+num);
			return mview;

	   }
	   

}
