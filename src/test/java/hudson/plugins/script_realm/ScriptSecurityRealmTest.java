package hudson.plugins.script_realm;

import java.io.File;
import org.jvnet.hudson.test.HudsonTestCase;
import org.springframework.ldap.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Kohsuke Kawaguchi
 */
public class ScriptSecurityRealmTest extends HudsonTestCase {

    private File trueScript = new File("src/test/resources/true.sh");
    private File falseScript = new File("src/test/resources/false.sh");

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (!trueScript.exists()) {
            throw new IllegalStateException(trueScript.getAbsolutePath() + " file not found!");
        }
        if (!falseScript.exists()) {
            throw new IllegalStateException(falseScript.getAbsolutePath() + " file not found!");
        }
        Runtime.getRuntime().exec("chmod 777 " + trueScript.getAbsolutePath());
        Runtime.getRuntime().exec("chmod 777 " + falseScript.getAbsolutePath());
    }

    public void test1() {
        UserDetails user = new ScriptSecurityRealm(trueScript.getAbsolutePath(), null, null).authenticate("test", "test");
        System.out.println("**-->" + user);
        assertTrue("user account not enabled", user.isEnabled());
        assertTrue("user credentials expired", user.isCredentialsNonExpired());
        assertTrue("user account locked", user.isAccountNonLocked());
        assertTrue("user account expired", user.isAccountNonExpired());
    }

    public void test2() {
        try {
            new ScriptSecurityRealm(falseScript.getAbsolutePath(), null, null).authenticate("test", "test");
            fail();
        } catch (BadCredentialsException e) {
            // as expected
        }
    }
}
