package com.wdy.module;

import com.wdy.module.dao.*;
import com.wdy.module.dynamicquery.DynamicQuery;
import com.wdy.module.entity.*;
import com.wdy.module.service.DispmsService;
import com.wdy.module.service.UserService;
import com.wdy.module.serviceUtil.MessageSender;
import com.wdy.module.serviceUtil.SpringContextUtil;
import com.wdy.module.utils.JWTTokenUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.Temporal;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ESLSApplicationTests {
    @Autowired
    private UserService userService;
    @Autowired
    private DynamicQuery dynamicQuery;
    @Autowired
    private StyleDao styleDao;
    @Autowired
    private GoodDao goodDao;
    @Autowired
    DispmsDao dispmsDao;

    @Test
    public void contextLoads() {
//        RabbiMqSendBean rabbiMqSendBean = new RabbiMqSendBean();
//        List<Tag> tags = tagService.findAll();
//        rabbiMqSendBean.setTags(tags);
//        rabbiMqSendBean.setIsWaiting(true);
//        rabbitMqSender.send(rabbiMqSendBean);

        //      MessageSender.sendMessageByApi();
        User user = userService.findById((long) 1);
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 123);
        claims.put("user", user);
        String jwtToken = JWTTokenUtil.createJWTToken(claims, (long) 10000);
        Map map = JWTTokenUtil.parseJWToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjFlOWFhYjUxLTdiNTgtNDZkNy1iMzU0LTdiZTJhNzUyZDlhYiIsInVzZXIiOnsiaWQiOjEsIm5hbWUiOiJFU0xTIiwicGFzc3dkIjoiODMzYzFlMDk4YTUzNTc1MDMzYzVlN2E5Nzg3NWI5ZjUiLCJyYXdQYXNzd2QiOiIiLCJ0ZWxlcGhvbmUiOiIxNzcyMjgyODEzNCIsImFkZHJlc3MiOiLlub_kuJznnIHmt7HlnLPluILljZflsbHljLrmt7HlnLPlpKflraYxIiwiZGVwYXJ0bWVudCI6IuWIm-WNjzEiLCJjcmVhdGVUaW1lIjoxNTUwNzU0MTIyMDAwLCJsYXN0TG9naW5UaW1lIjoxNTU1MDYxMzQ4MDAwLCJzdGF0dXMiOjEsImFjdGl2YXRlU3RhdHVzIjoxLCJtYWlsIjoiMTMwNTgxNDI4NjZAMTYzLmNvbSIsInNob3AiOnsiaWQiOjEwLCJ0eXBlIjowLCJudW1iZXIiOiJBMDAwNCIsImZhdGhlclNob3AiOiJBMDAwMSIsIm5hbWUiOiLmtKrmtarljJfliIblupciLCJtYW5hZ2VyIjoiamliayIsImFkZHJlc3MiOiLmt7HlnLMiLCJhY2NvdW50IjoiIiwicGFzc3dvcmQiOiIiLCJwaG9uZSI6IjE1MjIwMTgyMDE4Iiwicm91dGVycyI6W3siaWQiOjcsIm1hYyI6IjAyMDAwMGQwZmYzOCIsImlwIjoiMTkyLjE2OC4xLjMwIiwib3V0TmV0SXAiOiIxMTYuNy4yNDUuMTc5IiwicG9ydCI6NTc3NDMsImNoYW5uZWxJZCI6IjEiLCJzdGF0ZSI6MSwic29mdFZlcnNpb24iOiJWMS4wMCIsImZyZXF1ZW5jeSI6IjM4YTkwMDAwIiwiaGFyZFZlcnNpb24iOiJWMi4yMCIsImV4ZWNUaW1lIjo3MiwiYmFyQ29kZSI6IjMzMzMzMzMzMzMzMyIsImlzV29ya2luZyI6MCwiY29tcGxldGVUaW1lIjoxNTU1ODE1NDM3MDAwLCJzaG9wIjoxMH1dfSwicm9sZUxpc3QiOlt7ImlkIjoxLCJ0eXBlIjoi5pyA6auY5p2D6ZmQIiwibmFtZSI6IueuoeeQhuWRmCIsInBlcm1pc3Npb25zIjpbeyJpZCI6MSwibmFtZSI6Iuagh-etvumXqueBryIsInVybCI6Ii90YWcvbGlnaHQifSx7ImlkIjoyLCJuYW1lIjoi5re75Yqg5oiW5L-u5pS55L-h5oGvIiwidXJsIjoiL3VybD1wb3N0In0seyJpZCI6MywibmFtZSI6Iuagh-etvuWVhuWTgee7keWumiIsInVybCI6Ii90YWcvYmluZCJ9LHsiaWQiOjQsIm5hbWUiOiLns7vnu5_oj5zljZUiLCJ1cmwiOiIvdXJscz1nZXQifSx7ImlkIjo1LCJuYW1lIjoi6K6-572u57O757uf5Y-C5pWwIiwidXJsIjoiL2NvbW1vbi9zeXN0ZW1BcmdzIn0seyJpZCI6NiwibmFtZSI6IuWvvOWFpeaVsOaNruW6k-ihqCIsInVybCI6Ii9jb21tb24vZGF0YWJhc2UvaW1wb3J0In0seyJpZCI6NywibmFtZSI6IuWvvOWHuuaVsOaNruW6k-ihqCIsInVybCI6Ii9jb21tb24vZGF0YWJhc2UvZXhwb3J0In0seyJpZCI6OCwibmFtZSI6IuiOt-WPluaVsOaNruihqOS_oeaBryIsInVybCI6Ii9jb21tb24vZGF0YWJhc2UifSx7ImlkIjo5LCJuYW1lIjoi6I635Y-W5oyH5a6aSUTnmoTkv6Hmga8iLCJ1cmwiOiIvdXJsL3tpZH09Z2V0In0seyJpZCI6MTAsIm5hbWUiOiLliKDpmaTmjIflrppJROeahOS_oeaBryIsInVybCI6Ii91cmwve2lkfT1kZWxldGUifSx7ImlkIjoxMSwibmFtZSI6IuafpeivouWSjOaQnOe0ouWKn-iDvSIsInVybCI6Ii91cmwvc2VhcmNoPXBvc3QifSx7ImlkIjoxMiwibmFtZSI6IuWIh-aNoueKtuaAgSIsInVybCI6Ii91cmwvc3RhdHVzPXB1dCJ9LHsiaWQiOjEzLCJuYW1lIjoi5qCH562-5pu05o2i5qC35byPIiwidXJsIjoiL3RhZy9zdHlsZSJ9LHsiaWQiOjE0LCJuYW1lIjoi5qCH562-5Yi35pawIiwidXJsIjoiL3RhZy9mbHVzaCJ9LHsiaWQiOjE1LCJuYW1lIjoi5qCH562-5beh5qOAIiwidXJsIjoiL3RhZy9zY2FuIn0seyJpZCI6MTYsIm5hbWUiOiLmn6XnnIvmiYDmnInlj5jku7fotoXml7bnmoTmoIfnrb7kv6Hmga8iLCJ1cmwiOiIvdGFncy9vdmVydGltZSJ9LHsiaWQiOjE3LCJuYW1lIjoi5qCH562-56e76ZmkIiwidXJsIjoiL3RhZy9yZW1vdmUifSx7ImlkIjoxOCwibmFtZSI6IuabtOaUueaMh-WumklE5qC35byP55qE5bCP5qC35byPIiwidXJsIjoiL3N5dGxlL3VwZGF0ZSJ9LHsiaWQiOjE5LCJuYW1lIjoi5Yi35paw6YCJ55So6K-l5qC35byP55qE5qCH562-5oiW6K6-572u5a6a5pyf5Yi35pawIiwidXJsIjoiL3N0eWxlL2ZsdXNoIn0seyJpZCI6MjAsIm5hbWUiOiLllYblk4HmlLnku7ciLCJ1cmwiOiIvZ29vZC91cGRhdGUifSx7ImlkIjoyMSwibmFtZSI6IumAmui_h-WVhuWTgUlE6I635Y-W5YW257uR5a6a55qE5omA5pyJ5qCH562-5L-h5oGvIiwidXJsIjoiL2dvb2QvYmluZGVkIn0seyJpZCI6MjIsIm5hbWUiOiLorr7nva7llYblk4Hln7rmnKzmlbDmja7lkozllYblk4Hlj5jku7fmlofku7bot6_lvoTlj4pjcm9u6KGo6L6-5byP77yI5a6a5pyf5Lu75Yqh77yJIiwidXJsIjoiL2dvb2Qvc2NoZWR1bGUifSx7ImlkIjoyMywibmFtZSI6IuS4iuS8oOWVhuWTgeWfuuacrOaVsOaNruWPiuWPmOS7t-aVsOaNruaWh-S7tiIsInVybCI6Ii9nb29kL3VwbG9hZCJ9LHsiaWQiOjI0LCJuYW1lIjoiQVDmtYvor5UiLCJ1cmwiOiIvcm91dGVyL3Rlc3QifSx7ImlkIjoyNSwibmFtZSI6IuWPkemAgei3r-eUseWZqOiuvue9ruWRveS7pCIsInVybCI6Ii9yb3V0ZXIvc2V0dGluZyJ9LHsiaWQiOjI2LCJuYW1lIjoi5pu05o2i6Lev55Sx5ZmoIiwidXJsIjoiL3JvdXRlci9jaGFuZ2UifSx7ImlkIjoyNywibmFtZSI6Iui3r-eUseWZqOW3oeajgCIsInVybCI6Ii9yb3V0ZXIvc2NhbiJ9LHsiaWQiOjI4LCJuYW1lIjoi5L2_55So55S15a2Q56ek55u45YWzQVBJIiwidXJsIjoiL2JhbGFuY2UvZGF0YSJ9LHsiaWQiOjI5LCJuYW1lIjoi5paw5bu65oiW5L-u5pS55qC35byP5ZCM5pe257uR5a6a5bCP5qC35byPIiwidXJsIjoiL3N0eWxlL25ldyJ9LHsiaWQiOjMwLCJuYW1lIjoi55Sf5oiQ5oyH5a6aSUTmoLflvI_nmoTmiYDmnInlsI_moLflvI_lm77niYciLCJ1cmwiOiIvc3R5bGUvcGhvdG8ve2lkfSJ9LHsiaWQiOjMxLCJuYW1lIjoi5Li65oyH5a6aSUTnmoTop5LoibLmt7vliqDmnYPpmZAiLCJ1cmwiOiIvcm9sZS9hZGRQZXJtL3tpZH0ifSx7ImlkIjozMiwibmFtZSI6IuS4uuaMh-WumklE55qE55So5oi35re75Yqg6KeS6ImyIiwidXJsIjoiL3JvbGUvYWRkUm9sZS97aWR9In0seyJpZCI6MzMsIm5hbWUiOiLmoLnmja7op5LoibJJROiOt-W-l-adg-mZkCIsInVybCI6Ii9wZXJtaXNzaW9uL3JvbGUve2lkfSJ9LHsiaWQiOjM0LCJuYW1lIjoi6I635b6X5qCH562-5Y-v57uR5a6a55qE5omA5pyJ5qC35byPIiwidXJsIjoiL3RhZy9zdHlsZXMifSx7ImlkIjozNSwibmFtZSI6IuWvueaJgOaciei3r-eUseWZqOWPkei1t-W3oeajgCIsInVybCI6Ii9yb3V0ZXJzL3NjYW4ifSx7ImlkIjozNiwibmFtZSI6IuWIoOmZpOaMh-WumklE55qE6KeS6Imy55qE5a-55bqU5p2D6ZmQIiwidXJsIjoiL3JvbGUvZGVsUGVybSJ9LHsiaWQiOjM3LCJuYW1lIjoi6K6-572u6L-c56iL5pyN5Yqh5Zmo5L-h5oGvIiwidXJsIjoiL3JvdXRlci9yZW1vdGUifSx7ImlkIjozOCwibmFtZSI6IuiOt-W-l-ezu-e7n-WPguaVsCIsInVybCI6Ii9jb21tb24vc3lzdGVtQXJncyJ9LHsiaWQiOjM5LCJuYW1lIjoi5Y-R6YCB6Lev55Sx5Zmo56e76Zmk5ZG95LukIiwidXJsIjoiL3JvdXRlci9yZW1vdmUifSx7ImlkIjo0MCwibmFtZSI6IuaUueWPmOWumuaXtuS7u-WKoeeKtuaAgSIsInVybCI6Ii9jeWNsZWpvYi9zdGF0dXMifSx7ImlkIjo0MSwibmFtZSI6IuS4iuS8oOWNleS4quaWh-S7tiIsInVybCI6Ii91cGxvYWRGaWxlIn1dfV19LCJleHAiOjE1NTYyNjM3ODV9.lhYFuFPXaR5m9z0xZh2w4k8xGXTHFyTY4kgpfeyY6Io");
        System.out.println(map.get("user"));
        System.out.println(map.get("id"));
    }

    @Test
    public void testJPA() {
        String sql = "SELECT * FROM T_User WHERE name=?";
        User user = dynamicQuery.nativeQuerySingleResult(User.class, sql, new Object[]{"ESLS"});
//        System.out.println(user.getName());
//        dynamicQuery.save(user);
    }

    @Test
    public void testStyle() {
        List<Style> byWidthOrWidthOrderByStyleNumber = styleDao.findByWidthOrWidthOrderByStyleNumber(212, 250);
        List<Style> byWidthOrderByStyleNumber = styleDao.findByWidthOrderByStyleNumber(296);
    }

    @Test
    public void testGood() {
        Good good = goodDao.findById((long) 2).get();
        if (good.getPrice().contains("."))
            System.out.println(good.getPrice().substring(0, good.getPrice().indexOf(".") + 3));

    }

    @Test
    public void testSyleService() {
        Style style = Style.builder().id((long) 350).styleType("名字").isPromote((byte) 0).styleNumber("2988").width(250).height(250).build();
        styleDao.save(style);
    }

    @Test
    public void testMessage() throws Exception {
//        String p = "1 2 3";
//        MessageSender.sendMsgByTxPlatform("17722828134", p.split(" "));
        List<Style> byStyleNumber = styleDao.findByStyleNumber("2101");
        List<Dispms> dispmses = (List<Dispms>) byStyleNumber.get(0).getDispmses();
        dispmses.sort((a, b) -> (int) (a.getRegionId() - b.getRegionId()));
        System.out.println(dispmses);
    }
}
