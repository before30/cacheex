package cc.before30.metricex.cacheex.controller;

import cc.before30.metricex.cacheex.service.HelloService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloController
 *
 * @author before30
 * @since 2020/07/16
 */
@RestController
@AllArgsConstructor
public class HelloController {

    private final HelloService helloService;

    @GetMapping("/hello")
    public String hello() {
        return "world";
    }

    @GetMapping("/put/{id}")
    public String putId(@PathVariable Long id) {
        return helloService.put(id);
    }

    @GetMapping("/delete/{id}")
    public String deleteId(@PathVariable Long id) {
        return helloService.delete(id);
    }

    @GetMapping("/put2/{id}")
    public String put2Id(@PathVariable Long id) {
        return helloService.put2(id);
    }

    @GetMapping("/put3/{id}")
    public String put3Id(@PathVariable Long id) {
        return helloService.put3(id);
    }

    @GetMapping("/delete3/{id}")
    public String delete3Id(@PathVariable Long id) {
        return helloService.delete3(id);
    }

}
