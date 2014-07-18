package cab.data.ingest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/movie")
public class MovieController {

	
	
	@RequestMapping(value="/{name}", method = RequestMethod.GET)
	public @ResponseBody Employee  getMovie(@PathVariable String name, ModelMap model) {

		model.addAttribute("movie", name);
		return new Employee("sujan");

	}
	
}