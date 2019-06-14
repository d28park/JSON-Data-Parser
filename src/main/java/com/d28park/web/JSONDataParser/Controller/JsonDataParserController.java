package com.d28park.web.JSONDataParser.Controller;

import com.d28park.web.JSONDataParser.JSONToolModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Date;

@Controller
public class JsonDataParserController {
    private String appMode;

    @Autowired
    public JsonDataParserController(Environment environment){
        appMode = environment.getProperty("app-mode");
    }

    @RequestMapping("/")
    public String homePage(Model model){
        model.addAttribute("datetime", new Date(1560140925000L));
        model.addAttribute("fullname", "Daniel Park");

        model.addAttribute("mode", appMode);

        return "home";
    }

    @GetMapping("/tool")
    public String toolPage(Model model) {
        model.addAttribute("toolModel", new JSONToolModel());
        model.addAttribute("mode", appMode);

        return "tool";
    }

/*    @PostMapping(value = "/tool", params = "action=metadata")
    public ModelAndView getMetadata(@ModelAttribute(value="toolModel") JSONToolModel toolModel, BindingResult bindingResult) throws IOException {
        toolModel.generateMetadata();
        //String[] queryResults = toolModel.generateQueryResults();

        ModelAndView mav = new ModelAndView();
        if (bindingResult.hasErrors()) {
            mav.addObject("toolModel", toolModel);
            mav.addObject("mode", appMode);
        }

        return mav;
    }

    @PostMapping(value = "/tool", params = "action=query")
    public ModelAndView getQueryResults(@ModelAttribute(value="toolModel") JSONToolModel toolModel, BindingResult bindingResult) throws IOException {
        toolModel.generateQueryResults();

        ModelAndView mav = new ModelAndView();
        if (bindingResult.hasErrors()) {
            mav.addObject("toolModel", toolModel);
            mav.addObject("mode", appMode);
        }

        return mav;
    }*/
    @PostMapping(value = "/tool", params = "action=metadata")
    public String getMetadata(@ModelAttribute(value="toolModel") JSONToolModel toolModel, Model model) throws IOException {
        toolModel.generateMetadata();
        model.addAttribute("toolModel", toolModel);

        return "tool";
    }

    @PostMapping(value = "/tool", params = "action=query")
    public String getQueryResults(@ModelAttribute(value="toolModel") JSONToolModel toolModel, Model model) throws IOException {
        toolModel.generateQueryResults();
        model.addAttribute("toolModel", toolModel);

        return "tool";
    }
}