package net.fenghaitao;

import net.fenghaitao.utils.ConvertUtil;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;

public class ConvertUtilTest {
    @Test
    public void Convert() throws ParseException {
        Object in;
        Object out;

        in = "abc";
        out = ConvertUtil.convert(String.class, in);
        assertTrue(out instanceof String);
        assertEquals("abc", out);

        in = new Integer(12);
        out = ConvertUtil.convert(String.class, in);
        assertTrue(out instanceof String);
        assertEquals("12", out);

        in = new Date();
        out = ConvertUtil.convert(String.class, in);
        assertTrue(out instanceof String);
        assertEquals(in.toString(), out);

        //assertThrows()

        String big = "1234567890123456789012345678901234567890123456789012345678";
        in = big;
        out = ConvertUtil.convert(BigDecimal.class, in);
        assertTrue(out instanceof BigDecimal);
        assertEquals(new BigDecimal(big), out);

        in = "12";
        out = ConvertUtil.convert(Integer.class, in);
        assertTrue(out instanceof Integer);
        assertEquals(12, out);

        in = "12";
        out = ConvertUtil.convert(Long.class, in);
        assertTrue(out instanceof Long);
        assertEquals(12L, out);

        in = "12";
        out = ConvertUtil.convert(Short.class, in);
        assertTrue(out instanceof Short);
        assertEquals((short) 12, out);

        in = "12";
        out = ConvertUtil.convert(Byte.class, in);
        assertTrue(out instanceof Byte);
        assertEquals((byte) 12, out);

        in = "12.0";
        out = ConvertUtil.convert(Float.class, in);
        assertTrue(out instanceof Float);
        assertEquals(12.0f, out);

        in = "12.0";
        out = ConvertUtil.convert(Double.class, in);
        assertTrue(out instanceof Double);
        assertEquals(12.0d, out);

        in = "true";
        out = ConvertUtil.convert(Boolean.class, in);
        assertTrue(out instanceof Boolean);
        assertEquals(true, out);

        in = "c";
        out = ConvertUtil.convert(Character.class, in);
        assertTrue(out instanceof Character);
        assertEquals('c', out);

    }
}
