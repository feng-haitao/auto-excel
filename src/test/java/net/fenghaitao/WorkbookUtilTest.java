package net.fenghaitao;

import net.fenghaitao.utils.WorkbookUtil;
import org.junit.Assert;
import org.junit.Test;

public class WorkbookUtilTest {
    @Test
    public void indexToColName() {
        Assert.assertEquals(WorkbookUtil.indexToColName(10), "K");
        Assert.assertEquals(WorkbookUtil.indexToColName(26), "AA");
    }
}
