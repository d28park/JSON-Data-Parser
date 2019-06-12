package com.d28park.web.JSONDataParser.Controller;

import com.d28park.web.JSONDataParser.InputJSON;
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
        model.addAttribute("inputJson", new InputJSON());
        model.addAttribute("metadata", "");
        model.addAttribute("mode", appMode);

        return "tool";
    }

    @PostMapping("/tool")
    public ModelAndView getInputJson(@ModelAttribute(value="inputJson") InputJSON inputJson, BindingResult bindingResult) throws IOException {
        String metadata = inputJson.generateMetadata();

        ModelAndView mav = new ModelAndView();
        if (bindingResult.hasErrors()) {
            mav.addObject("inputJson", inputJson);
            mav.addObject("mode", appMode);
        }
        mav.addObject("metadata", metadata);

        return mav;
    }
}