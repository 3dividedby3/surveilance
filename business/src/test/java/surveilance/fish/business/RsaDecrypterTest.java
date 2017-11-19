package surveilance.fish.business;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class RsaDecrypterTest {

    @Test
    public void testDecrypt() {
        RsaDecrypter rsaDecrypter = new RsaDecrypter();
        byte[] result = rsaDecrypter.decrypt("qt/5MxYu3bt7u+PZ6ldCNOwsrAZGPENI+IWZPJeGu+bYY2JL5nleQgVMbQz9L5igg3KX8ga4mm9KzIjlleNgaozSjvzjjV+aK6yPRJyRmYS86wHqCvkN2vW1tM6kKPZX7dArHtaKRc9dhWTeowIcdj/9WjmRTibEQdVGW5+d0Gk=".getBytes());
        
        assertEquals("Results must be identical"
                , "test"
                , new String(result));
        
        
        LinkedHashMap<String, String> a = new LinkedHashMap<String, String>(){
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return 3 < size();
            }
        };
        a.put("a", "a");
        a.put("b", "b");
        a.put("c", "c");
        a.put("d", "d");
    }
}
