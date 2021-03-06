package com.quhaodian.adminstore.controller.front;

import com.haoxuer.discover.area.data.entity.Area;
import com.haoxuer.discover.area.data.enums.AreaType;
import com.haoxuer.discover.area.data.service.AreaService;
import com.haoxuer.discover.web.controller.front.BaseController;
import com.haoxuer.lbs.qq.v1.builder.ServicesBuilder;
import com.haoxuer.lbs.qq.v1.domain.response.AreaItem;
import com.haoxuer.lbs.qq.v1.service.DistrictService;
import com.haoxuer.discover.data.page.Filter;
import com.haoxuer.discover.data.page.Order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class SiteController extends BaseController {

    /**
     * 跳转登录页
     *
     * @return
     */
    @GetMapping(value = "/index")
    public String index() {
        return getView("index");
    }

    @GetMapping(value = "/de")
    public String de() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                f();
            }
        }).start();
        ;
        return getView("index");
    }

    @Autowired
    AreaService areaService;

    private void f() {
        DistrictService service = ServicesBuilder
                .newBuilder()
                .key("H4DBZ-WLVCU-YLEVF-4MIDF-MGB5H-TOFDR")
                .build()
                .getDistrictService();

        List<Filter> filers = new ArrayList<>();
        filers.add(Filter.eq("areaType", AreaType.country));
        List<Order> orders = new ArrayList<>();
        orders.add(Order.asc("code"));
        List<Area> areas = areaService.list(0, 90000, filers, orders);
        for (Area root : areas) {
            if (root.getGovCode().length() != 6) {
                continue;
            }

            List<AreaItem> areaItems = service.child(root.getGovCode()).getResult().get(0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (AreaItem areaItem : areaItems) {
                Area area = new Area();
                area.setParent(root);
                area.setName(areaItem.getName());
                area.setLat(areaItem.getLocation().getLat());
                area.setLng(areaItem.getLocation().getLng());
                area.setCode(areaItem.getId());
                area.setGovCode(areaItem.getId());
                area.setFullName(areaItem.getFullname());
                if (area.getName() == null) {
                    area.setName(area.getFullName());
                }
                area.setAreaType(AreaType.street);
                areaService.save(area);
            }
        }
    }

}
