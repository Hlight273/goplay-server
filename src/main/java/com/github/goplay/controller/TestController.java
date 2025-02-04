package com.github.goplay.controller;

import com.github.goplay.entity.TestItem;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @GetMapping("/hi")
    public String Hi(String hi){
        return hi+"泥壕啊";
    }

    //当我们加了@RequestParam("username")来指定前端访问地址所带参数名，这个参数就是必须的，否则后端不会返回，前端收到的也是404
    //当然，你也可以诶改成@RequestParam("username",required = false)来让它不必须
    @GetMapping("/hello")
    public String Hello(@RequestParam(value = "username") String name){
        System.out.println("有人发送了hello get请求");
        return name+",Hello啊";
    }

    @RequestMapping(value = "/giveme", method = RequestMethod.POST)
    public String GiveMe(String itemname){
        return "后端收到了一个"+itemname;
    }

    //用面向对象的方式解耦
    //顺带一提这个实体类，必须要有一个无参构造函数
    @Tag(name = "发送物品", description = "这是一个案例OpenAPI/Swagger3的api注释")
    //Spring Boot 启动时就会自动启用 Swagger, 从以下地址可以访问 接口形式(JSON, YAML)和WEB形式的接口文档
    //
    //http://host:port/context-path/v3/api-docs
    //YAML格式 http://host:port/context-path/v3/api-docs.yaml
    //http://host:port/context-path/swagger-ui/index.html
    @RequestMapping(value = "/postitem", method = RequestMethod.POST)
    public String PostItem(TestItem tItem){
        System.out.println(tItem.toString());
        return "后端收到了一个测试item："+tItem.getItemname();
    }

    //当我们给参数指定@RequestBody注解，表示只接收json格式数据
    //关于json的细节，例如"num":11和num:"11"，这在json里面是不同的数据类型。我们尽量要保证json数据类型和实体类中的数据类型 保 持 一 致
    @RequestMapping(value = "/postjitem", method = RequestMethod.POST)
    public String PostJsonItem(@RequestBody TestItem tItem){
        System.out.println(tItem.toString());
        return "前端给后端发送了一个json格式的testitem对象，后端成功收到了一个测试item："+tItem.getItemname();
    }
}
