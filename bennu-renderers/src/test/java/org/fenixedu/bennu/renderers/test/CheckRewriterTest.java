package org.fenixedu.bennu.renderers.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

@RunWith(JUnit4.class)
public class CheckRewriterTest {

    private final GenericChecksumRewriter EMPTY_REWRITER = new GenericChecksumRewriter(null);

    @Test
    public void linkTagWithoutHref() {
        checkNoChange("<a>X</a>");
    }

    @Test
    public void hashtagIsNotOverwritten() {
        assertThat(EMPTY_REWRITER.rewrite("<a href=\"xpto#x\">X</a>"),
                is("<a href=\"xpto?_request_checksum_=7788b911f74d17e780634c375a45a007beaeae27#x\">X</a>"));
    }

    @Test
    public void httpLinkTag() {
        checkNoChange("<a href=\"http://fenixedu.org\">X</a>");
    }

    @Test
    public void httpsLinkTag() {
        checkNoChange("<a href=\"https://fenixedu.org\">X</a>");
    }

    @Test
    public void mailtoLinkTag() {
        checkNoChange("<a href=\"mailto:hello@fenixedu.org\">X</a>");
    }

    @Test
    public void javascriptLinkTag() {
        checkNoChange("<a href=\"javascript:alert('FenixEdu');\">X</a>");
    }

    @Test
    public void linkTagWithoutHrefDoesntPreventFurtherInjection() {
        assertThat(EMPTY_REWRITER.rewrite("<a>X</a> <a href=\"xpto\">x</a>"),
                is("<a>X</a> <a href=\"xpto?_request_checksum_=7788b911f74d17e780634c375a45a007beaeae27\">x</a>"));
    }

    @Test
    public void imgTagWithoutSrc() {
        checkNoChange("<img alt=\"\" />");
    }

    @Test
    public void imgTagWithoutSrcDoesntPreventFurtherInjection() {
        assertThat(EMPTY_REWRITER.rewrite("<img alt=\"\" /> <a href=\"xpto\">x</a>"),
                is("<img alt=\"\" /> <a href=\"xpto?_request_checksum_=7788b911f74d17e780634c375a45a007beaeae27\">x</a>"));
    }

    @Test
    public void formTagWithoutAction() {
        checkNoChange("<form method=\"POST\"></form>");
    }

    @Test
    public void formTagWithoutActionDoesntPreventFurtherInjection() {
        assertThat(
                EMPTY_REWRITER.rewrite("<form method=\"POST\"></form> <a href=\"xpto\">x</a>"),
                is("<form method=\"POST\"></form> <a href=\"xpto?_request_checksum_=7788b911f74d17e780634c375a45a007beaeae27\">x</a>"));
    }

    @Test
    public void malformedATagDoesntPreventFurtherInjection() {
        assertThat(
                EMPTY_REWRITER.rewrite("<a <form action=\"xpto\">x</form>"),
                is("<a <form action=\"xpto\"><input type=\"hidden\" name=\"_request_checksum_\" value=\"7788b911f74d17e780634c375a45a007beaeae27\"/>x</form>"));
    }

    @Test
    public void noChecksum() {
        checkNoChange("<!-- NO_CHECKSUM --><a href=\"xpto\">x</a>");
    }

    private void checkNoChange(String value) {
        assertThat(EMPTY_REWRITER.rewrite(value), is(value));
    }

}
