package org.xwiki.diff.internal;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.diff.DiffManager;
import org.xwiki.diff.MergeResult;
import org.xwiki.logging.LogLevel;
import org.xwiki.test.mockito.MockitoComponentMockingRule;


public class AmplDefaultDiffManagerTest {
    @Rule
    public final MockitoComponentMockingRule<DiffManager> mocker = new MockitoComponentMockingRule<DiffManager>(DefaultDiffManager.class);

    private static List<Character> toCharacters(String str) {
        List<Character> characters;
        if (str != null) {
            characters = new ArrayList<Character>(str.length());
            for (char c : str.toCharArray()) {
                characters.add(c);
            }
        }else {
            characters = Collections.emptyList();
        }
        return characters;
    }

    private static String toString(List<Character> characters) {
        return StringUtils.join(characters, null);
    }

    /* amplification of org.xwiki.diff.internal.DefaultDiffManagerTest#testMergeCharList */
    @Test(timeout = 10000)
    public void testMergeCharList_examplifier28() throws Exception {
        MergeResult<Character> result;
        // New empty
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("a"), AmplDefaultDiffManagerTest.toCharacters(""), AmplDefaultDiffManagerTest.toCharacters("b"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__9 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(1, ((int) (o_testMergeCharList_examplifier28__9)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__12 = AmplDefaultDiffManagerTest.toCharacters("b");
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__13 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__13.contains('b'));
        // New before
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("bc"), AmplDefaultDiffManagerTest.toCharacters("abc"), AmplDefaultDiffManagerTest.toCharacters("bc"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__21 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__21)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__24 = AmplDefaultDiffManagerTest.toCharacters("abc");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__24.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__24.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__24.contains('c'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__25 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__25.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__25.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__25.contains('c'));
        // New after
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("ab"), AmplDefaultDiffManagerTest.toCharacters("abc"), AmplDefaultDiffManagerTest.toCharacters("ab"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__33 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__33)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__36 = AmplDefaultDiffManagerTest.toCharacters("abc");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__36.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__36.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__36.contains('c'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__37 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__37.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__37.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__37.contains('c'));
        // New middle
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("ac"), AmplDefaultDiffManagerTest.toCharacters("abc"), AmplDefaultDiffManagerTest.toCharacters("ac"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__45 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__45)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__48 = AmplDefaultDiffManagerTest.toCharacters("abc");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__48.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__48.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__48.contains('c'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__49 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__49.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__49.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__49.contains('c'));
        // Before and after
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("b"), AmplDefaultDiffManagerTest.toCharacters("ab"), AmplDefaultDiffManagerTest.toCharacters("bc"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__57 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__57)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__60 = AmplDefaultDiffManagerTest.toCharacters("abc");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__60.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__60.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__60.contains('c'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__61 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__61.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__61.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__61.contains('c'));
        // After and before
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("b"), AmplDefaultDiffManagerTest.toCharacters("bc"), AmplDefaultDiffManagerTest.toCharacters("ab"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__69 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__69)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__72 = AmplDefaultDiffManagerTest.toCharacters("abc");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__72.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__72.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__72.contains('c'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__73 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__73.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__73.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__73.contains('c'));
        // Insert current and next
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("abc"), AmplDefaultDiffManagerTest.toCharacters("aibc"), AmplDefaultDiffManagerTest.toCharacters("abcj"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__81 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__81)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__84 = AmplDefaultDiffManagerTest.toCharacters("aibcj");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__84.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__84.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__84.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__84.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__84.contains('j'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__85 = result.getMerged();
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("abc"), AmplDefaultDiffManagerTest.toCharacters("abcj"), AmplDefaultDiffManagerTest.toCharacters("aibc"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__92 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__92)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__95 = AmplDefaultDiffManagerTest.toCharacters("aibcj");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__95.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__95.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__95.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__95.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__95.contains('j'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__96 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__96.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__96.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__96.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__96.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__96.contains('j'));
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("abc"), AmplDefaultDiffManagerTest.toCharacters("ajbc"), AmplDefaultDiffManagerTest.toCharacters("aibc"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__103 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(1, ((int) (o_testMergeCharList_examplifier28__103)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__106 = AmplDefaultDiffManagerTest.toCharacters("ajibc");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__106.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__106.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__106.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__106.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__106.contains('c'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__107 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__107.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__107.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__107.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__107.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__107.contains('c'));
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("ab"), AmplDefaultDiffManagerTest.toCharacters("aijb"), AmplDefaultDiffManagerTest.toCharacters("aib"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__114 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(1, ((int) (o_testMergeCharList_examplifier28__114)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__117 = AmplDefaultDiffManagerTest.toCharacters("aijb");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__117.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__117.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__117.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__117.contains('b'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__118 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__118.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__118.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__118.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__118.contains('b'));
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("ab"), AmplDefaultDiffManagerTest.toCharacters("ajb"), AmplDefaultDiffManagerTest.toCharacters("aijb"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__125 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(1, ((int) (o_testMergeCharList_examplifier28__125)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__128 = AmplDefaultDiffManagerTest.toCharacters("aijb");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__128.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__128.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__128.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__128.contains('b'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__129 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__129.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__129.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__129.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__129.contains('b'));
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters(""), AmplDefaultDiffManagerTest.toCharacters("ab"), AmplDefaultDiffManagerTest.toCharacters("abc"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__136 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__136)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__139 = AmplDefaultDiffManagerTest.toCharacters("abc");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__139.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__139.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__139.contains('c'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__140 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__140.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__140.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__140.contains('c'));
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters(""), AmplDefaultDiffManagerTest.toCharacters("abc"), AmplDefaultDiffManagerTest.toCharacters("ab"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__147 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__147)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__150 = AmplDefaultDiffManagerTest.toCharacters("abc");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__150.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__150.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__150.contains('c'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__151 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__151.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__151.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__151.contains('c'));
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters(""), AmplDefaultDiffManagerTest.toCharacters("bc"), AmplDefaultDiffManagerTest.toCharacters("abc"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__158 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__158)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__161 = AmplDefaultDiffManagerTest.toCharacters("abc");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__161.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__161.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__161.contains('c'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__162 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__162.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__162.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__162.contains('c'));
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters(""), AmplDefaultDiffManagerTest.toCharacters("abc"), AmplDefaultDiffManagerTest.toCharacters("bc"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__169 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__169)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__172 = AmplDefaultDiffManagerTest.toCharacters("abc");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__172.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__172.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__172.contains('c'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__173 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__173.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__173.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__173.contains('c'));
        // Misc
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("Alice Macro"), AmplDefaultDiffManagerTest.toCharacters("Alice Wiki Macro (upgraded)"), AmplDefaultDiffManagerTest.toCharacters("Alice Extension"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__181 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__181)));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__184 = AmplDefaultDiffManagerTest.toCharacters("Alice Wiki Extension (upgraded)");
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('A'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('l'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('e'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains(' '));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('W'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('k'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains(' '));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('E'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('x'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('t'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('e'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('n'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('s'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('o'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('n'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains(' '));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('('));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('u'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('p'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('g'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('r'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('d'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('e'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('d'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains(')'));
        // AssertGenerator create local variable with return value of invocation
        List<Character> o_testMergeCharList_examplifier28__185 = result.getMerged();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('A'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('l'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('e'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains(' '));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('W'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('k'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains(' '));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('E'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('x'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('t'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('e'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('n'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('s'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('o'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('n'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains(' '));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('('));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('u'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('p'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('g'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('r'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('d'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('e'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('d'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains(')'));
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("$a(b)"), AmplDefaultDiffManagerTest.toCharacters("$c(d)e"), AmplDefaultDiffManagerTest.toCharacters("$c(d)e"), null);
        // AssertGenerator create local variable with return value of invocation
        int o_testMergeCharList_examplifier28__192 = result.getLog().getLogs(LogLevel.ERROR).size();
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__192)));
        // AssertGenerator create local variable with return value of invocation
        String o_testMergeCharList_examplifier28__195 = AmplDefaultDiffManagerTest.toString(result.getMerged());
        // AssertGenerator add assertion
        Assert.assertEquals("$c(d)e", o_testMergeCharList_examplifier28__195);
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('A'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('l'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('e'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains(' '));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('W'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('k'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains(' '));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('E'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('x'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('t'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('e'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('n'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('s'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('o'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('n'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains(' '));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('('));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('u'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('p'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('g'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('r'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('d'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('e'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains('d'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__184.contains(')'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__84.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__84.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__84.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__84.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__84.contains('j'));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__81)));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__21)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__61.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__61.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__61.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__24.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__24.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__24.contains('c'));
        // AssertGenerator add assertion
        Assert.assertEquals(1, ((int) (o_testMergeCharList_examplifier28__114)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__151.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__151.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__151.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__161.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__161.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__161.contains('c'));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__181)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__117.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__117.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__117.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__117.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__49.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__49.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__49.contains('c'));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__136)));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__192)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__36.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__36.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__36.contains('c'));
        // AssertGenerator add assertion
        Assert.assertEquals(1, ((int) (o_testMergeCharList_examplifier28__9)));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__92)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__129.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__129.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__129.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__129.contains('b'));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__169)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__106.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__106.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__106.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__106.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__106.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__107.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__107.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__107.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__107.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__107.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__37.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__37.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__37.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__140.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__140.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__140.contains('c'));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__147)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__25.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__25.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__25.contains('c'));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__158)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__48.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__48.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__48.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__13.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__118.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__118.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__118.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__118.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__128.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__128.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__128.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__128.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__95.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__95.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__95.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__95.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__95.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__60.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__60.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__60.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__12.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__173.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__173.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__173.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__162.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__162.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__162.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__150.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__150.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__150.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('A'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('l'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('e'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains(' '));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('W'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('k'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains(' '));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('E'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('x'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('t'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('e'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('n'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('s'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('o'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('n'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains(' '));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('('));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('u'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('p'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('g'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('r'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('d'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('e'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains('d'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__185.contains(')'));
        // AssertGenerator add assertion
        Assert.assertEquals(1, ((int) (o_testMergeCharList_examplifier28__125)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__96.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__96.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__96.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__96.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__96.contains('j'));
        // AssertGenerator add assertion
        Assert.assertEquals(1, ((int) (o_testMergeCharList_examplifier28__103)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__139.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__139.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__139.contains('c'));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__69)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__72.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__72.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__72.contains('c'));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__57)));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__45)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__85.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__85.contains('i'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__85.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__85.contains('c'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__85.contains('j'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__172.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__172.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__172.contains('c'));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (o_testMergeCharList_examplifier28__33)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__73.contains('a'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__73.contains('b'));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testMergeCharList_examplifier28__73.contains('c'));
    }
}

