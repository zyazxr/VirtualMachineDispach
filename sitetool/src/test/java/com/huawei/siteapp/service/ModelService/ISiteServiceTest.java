package com.huawei.siteapp.service.ModelService;

import com.huawei.siteapp.MappRunApplication;
import com.huawei.siteapp.service.ModelService.Impl.SiteServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by z00390414 on 2017/7/6.
 *
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MappRunApplication.class)
public class ISiteServiceTest {
    @Autowired
    SiteServiceImpl siteService;

    @Test
    public void findById2() throws Exception {
//      SiteModel siteModel =  querySites.findByEmailAddress("admin");

//      System.out.println("###########"+siteModel.toString());
//        SiteModel siteModel = siteService.findSiteModelBySiteLoginUser("admin");
//        System.out.println("###########"+siteModel);
    }

}