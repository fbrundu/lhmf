package it.polito.ai.lhmf.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PictureUploadController {
	//FIXME rimuovere questo controller. è solo di prova. rimuoverlo anche dal context-security (è tra le prime cose a security:none)
	
	
	@RequestMapping(value="/productPhoto", method=RequestMethod.POST)
	public ModelAndView pictureUpload(Model model, HttpServletRequest request, @RequestParam("file") MultipartFile file){
		ServletContext context = request.getServletContext();
		
		String path = context.getRealPath("/img/prd/");
		String fileName = file.getOriginalFilename();
		File f = new File(path + File.separator + fileName);
		OutputStream writer = null;
		try {
			writer = new BufferedOutputStream(new FileOutputStream(f));
			writer.write(file.getBytes());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Da errore...tanto non useremo questa
		return new ModelAndView("login");
	}
}
