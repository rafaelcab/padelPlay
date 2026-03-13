
package com.padelplay.server.facade;

import com.padelplay.common.dto.PruebaDto; //no entiendo porque la direccion es asi
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String test() {
        return "Servidor funcionando";
    }
}