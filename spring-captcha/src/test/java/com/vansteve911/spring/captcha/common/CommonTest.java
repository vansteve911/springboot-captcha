package com.vansteve911.spring.captcha.common;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by vansteve911 on 18/4/6.
 */
@RunWith(SpringRunner.class)
public class CommonTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void genRandomInt() throws Exception {
        for (int i = 0; i < 5; i++) {
            int lower = (int) (System.nanoTime() % 10000);
            int upper = (int) (System.nanoTime() % 10000);
            if (lower > upper) {
                int tmp = lower;
                lower = upper;
                upper = tmp;
            }
            System.out.println(lower + "," + upper);
            int rnd = CommonUtils.genRandomInt(lower, upper);
            assertTrue(rnd <= upper && rnd >= lower);
        }
    }

    @Test
    public void captchaExceptionSetMsgTypesTest() {
        Map<CaptchaException.Type, String> map = new HashMap<>();
        for (CaptchaException.Type type : CaptchaException.Type.values()) {
            map.put(type, type.name().toLowerCase());
        }
        CaptchaException.setMsgTypes(map);
        for (CaptchaException.Type type : CaptchaException.Type.values()) {
            assertEquals(type.name().toLowerCase(), CaptchaException.typeMsg(type));
        }
        map.remove(CaptchaException.Type.GENERATE_FAILED);
        thrown.expect(IllegalArgumentException.class);
        CaptchaException.setMsgTypes(map);
    }

}