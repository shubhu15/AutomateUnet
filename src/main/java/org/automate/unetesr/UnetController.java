package org.automate.unetesr;

import java.io.IOException;

import org.automate.excelgeneration.excelRw;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UnetController {
	
	String curr_date = null;
	String comment =null;
	
//testing
	@RequestMapping("/unet")
	private String getUnetEta() {
		return "eta is calculated";
	}
	
//ETA Calculation
	@RequestMapping("/{user_input_currdate}/{user_input_prevdate}")
	private String getUnetEsrEta(@PathVariable String user_input_currdate,@PathVariable String user_input_prevdate) throws IOException {
		
		System.out.println("user_input_currdate" +user_input_currdate );
		System.out.println("user_input_prevdate" +user_input_prevdate );
		excelRw excel = new excelRw(user_input_prevdate,user_input_currdate);
		return "generated the UNET ESR";
		
	}
}
